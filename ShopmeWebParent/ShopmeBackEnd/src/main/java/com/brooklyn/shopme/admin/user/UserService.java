package com.brooklyn.shopme.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import javax.sound.midi.VoiceStatus;
import javax.transaction.Transactional;

import org.aspectj.weaver.ast.Instanceof;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.brooklyn.shopme.common.entity.Role;
import com.brooklyn.shopme.common.entity.User;

@Service
@Transactional
public class UserService {
	public static final int USERS_PER_PAGE = 4;
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	public User findByEmail(String email){
		return userRepository.findByEmail(email);
	}
	public List<User> listAll()
	{
		return (List<User>) userRepository.findAll();
	}
	public Page<User> listByPage(int pageNum, String sortField, String orderBy,String keyword){
		Sort sort = Sort.by(sortField);
		sort = orderBy.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE,sort);
		if(keyword != null)
		{
			return userRepository.findAll(keyword,pageable);
		}
		return userRepository.findAll(pageable);
	}
	public User save(User theUser)
	{
		if(theUser.getId() != null)
		{
			// updating
			User existingUser = userRepository.findById(theUser.getId()).get();
			if(theUser.getPassword().isEmpty())
				// leave blank so u can simply set the password in database
			{
				theUser.setPassword(existingUser.getPassword());
			}
			else {
				// if not blank we have no encode it again before saving
				encodePassword(theUser);
			}
		}
		else {
			// new user just encode it
			encodePassword(theUser);
		}
		
		 return userRepository.save(theUser);
	}
	public User updateAccount(User userInForm){
		User userInDb = userRepository.findById(userInForm.getId()).get();
		if(!userInForm.getPassword().isEmpty())
		{
			userInDb.setPassword(userInForm.getPassword());
			encodePassword(userInDb);
		}
		if(userInForm.getPhotos() != null)
		{
			userInDb.setPhotos(userInForm.getPhotos());
		}
		userInDb.setFirstName(userInForm.getFirstName());
		userInDb.setLastName(userInForm.getLastName());
		return userRepository.save(userInDb);
		
	}
	public List<Role> listRole()
	{
		return (List<Role>) roleRepository.findAll();
	}
	private void encodePassword(User user)
	{
		user.setPassword(passwordEncoder.encode(user.getPassword()));
	}
	public boolean isEmailUnique(Integer id, String email)
	{
		User userByEmail = userRepository.findByEmail(email);
		if(userByEmail == null) {
			return true;
		}
		if(id == null)
			// is new user
		{
			if(userByEmail != null ) {
				// ton tai email
				return false;
			}
		}
		else 
			// is update
		{
			if(userByEmail.getId() != id) {
				return false;
			}
		}
		return true;
	}
	public User get(Integer id) throws UserNotFoundException {
		// TODO Auto-generated method stub
		try {
			return userRepository.findById(id).get();
			
		} catch (NoSuchElementException e) {
			throw new UserNotFoundException("Could not find any user with ID "+ id);
		}
	}
	public void delete(Integer id) throws UserNotFoundException
	{
		Long countById = userRepository.countById(id);
		if(countById == null || countById == 0)
		{
			throw new UserNotFoundException("Could not find any user with ID "+ id);
		}
		userRepository.deleteById(id);
	}
	public void updateUserStatus(Integer id, boolean status)
	{
		userRepository.updateEnabledStatus(id, status);
	}
	
}
