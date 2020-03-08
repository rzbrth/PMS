package com.rzb.pms.service;

import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rzb.pms.dto.AuditType;
import com.rzb.pms.dto.UserSignUpDTO;
import com.rzb.pms.dto.UserTokenType;
import com.rzb.pms.exception.CustomEntityNotFoundException;
import com.rzb.pms.exception.CustomException;
import com.rzb.pms.model.Audit;
import com.rzb.pms.model.Role;
import com.rzb.pms.model.RoleType;
import com.rzb.pms.model.UserToken;
import com.rzb.pms.model.Users;
import com.rzb.pms.repository.AuditRepository;
import com.rzb.pms.repository.RoleRepository;
import com.rzb.pms.repository.UserRepository;
import com.rzb.pms.repository.UserTokenRepository;
import com.rzb.pms.utils.BaseUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

	@Autowired
	private AuditRepository auditRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserTokenRepository userTokenRepository;

	@Autowired
	private RoleService roleService;

	@Value("${app.domain}")
	private String websiteURL;

	@Autowired
	private EmailService emailService;

	@Autowired
	private RoleRepository roleRepo;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public Users findByUsername(String username) {
		return userRepository.findByUserName(username);
	}

	public Users findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public void save(Users users) {
		userRepository.save(users);
	}

	public boolean existUserByEmail(String email) {
		return userRepository.existsUsersByEmail(email);
	}

	public boolean existUserPhone(String phone) {
		return userRepository.existsUsersByPhone(phone);
	}

	@Transactional
	public String registerUser(UserSignUpDTO data) {

		if (data == null) {
			throw new CustomException("Users inforamation can not be null or empty", HttpStatus.BAD_REQUEST);
		}

		if (existUserByEmail(data.getEmail()) || existUserPhone(data.getPhone())) {
			throw new CustomException("Users " + "[ Email:" + data.getEmail() + ", Phone:" + data.getPhone()
					+ "] already exist, Please use different email and phone number", HttpStatus.BAD_REQUEST);
		}
		RoleType role = null;
		List<Role> r = new ArrayList<Role>();
		// List<Users> users = new ArrayList<Users>();
		try {

			if (data.getUserRole().equalsIgnoreCase(RoleType.ADMIN.toString())) {
				role = RoleType.ADMIN;
				r = Collections.singletonList(createRoleIfNotFound(role));
			} else if (data.getUserRole().equalsIgnoreCase(RoleType.USER.toString())) {
				role = RoleType.USER;
				r = Collections.singletonList(createRoleIfNotFound(role));
			}

			Users u = Users.builder().email(data.getEmail()).isEnabled(false)
					.password(passwordEncoder.encode(data.getPassword())).phone(data.getPhone())
					.userName(data.getUserName()).roles(r).build();
			// users.add(u);
			userRepository.saveAndFlush(u);
			auditRepository.save(Audit.builder().auditType(AuditType.USER_CREATED.toString())
					.createdBy(data.getUserName()).createdDate(LocalDate.now()).userId(u.getId()).build());

			// if admin send verification email
			if ("ADMIN".equalsIgnoreCase(role.toString())) {
				String token = UUID.randomUUID().toString();
				saveUserToken(u, token, UserTokenType.EMAILVERIFICATION);
				// now send email
				String link = websiteURL + "/verifyEmail?email=" + URLEncoder.encode(u.getEmail()) + "&token=" + token;
				emailService.sendVerificationMail(u.getEmail(), u.getUserName(), link);
				log.info("Signup successful");
				return "An email has been sent for verification";

			}
			return "Registration Successful";

		} catch (Exception e) {
			throw new CustomException("Problem while registration", e);
		}
	}

	/**
	 * @param users
	 * @param token
	 * @param tokenType
	 */
	public void saveUserToken(final Users users, final String token, final UserTokenType tokenType) {
		final UserToken userToken = new UserToken(token, users);
		userToken.setType(tokenType);

		userTokenRepository.save(userToken);
	}

	public String manageAccountActiveStatus(Long userId, Boolean status) {

		if (userId == 0) {
			throw new CustomException("Users id can not be null or empty", HttpStatus.BAD_REQUEST);
		}
		Users users = userRepository.findById(userId).get();
		if (users == null) {
			throw new CustomEntityNotFoundException(Users.class, "Users Id", userId.toString());
		}
		try {
			users.setIsEnabled(status);
			if (users.getRoles().get(0).equals(RoleType.USER)) {
				userRepository.save(users);
				if (status) {
					auditRepository.save(Audit.builder().auditType(AuditType.USER_ENABLED.toString()).updatedBy("")
							.updatedDate(LocalDate.now()).userId(userId).build());
					return "Users Activated Successfully";
				} else {
					auditRepository.save(Audit.builder().auditType(AuditType.USER_DISABLED.toString()).updatedBy("")
							.updatedDate(LocalDate.now()).userId(userId).build());
					return "Users Deactivated Successfully";

				}
			} else {
				throw new CustomException("Can not manually set activation status for Admin", HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			throw new CustomException("Problem while activating user, Please try again or contact PILL-H team", e);
		}

	}

	@Transactional
	Role createRoleIfNotFound(RoleType name) {
		boolean existsRoleByName = roleService.existsRoleByName(name);
		if (!existsRoleByName) {
			Role role = new Role(name);
			roleService.save(role);
			return role;
		}
		return null;
	}

	/**
	 * @param token
	 * @param inputEmail
	 * @return Accepts token and email to verify and change status of user to
	 *         CONFIRMED
	 */
	@Transactional
	public String verifyEmail(String token, String inputEmail) {
		String email = validatePasswordResetToken(token).trim();
		Users users = userRepository.findByEmail(email);
		if (users == null) {
			log.error("Oops! Email not found in our system.");
			throw new CustomException("Oops! Email not found in our system.", HttpStatus.NOT_FOUND);
		}
		if (!inputEmail.equals(email)) {
			log.error("Invalid email");
			throw new CustomException("Invalid email", HttpStatus.BAD_REQUEST);
		}
		// set status as confirmed so that user can signin
		users.setIsEnabled(true);
		userRepository.save(users);

		return "Email verification successfull";
	}

	/**
	 * @param token
	 * @return Validate token and remove if valid
	 */
	public String validatePasswordResetToken(String token) {
		UserToken userToken = userTokenRepository.findByToken(token);

		if ((userToken == null)) {
			log.error("Invalid link");

			throw new CustomException("Invalid link", HttpStatus.BAD_REQUEST);
		}

		Calendar cal = Calendar.getInstance();
		if ((BaseUtil.convertToDateViaSqlDate(userToken.getExpiry()).getTime() - cal.getTime().getTime()) <= 0) {

			log.error("Invalid link");

			throw new CustomException("Invalid link", HttpStatus.BAD_REQUEST);
		}

		log.info("Valid token");

		// delete token
		userTokenRepository.delete(userToken);

		return userToken.getUsers().getEmail();
	}
}
