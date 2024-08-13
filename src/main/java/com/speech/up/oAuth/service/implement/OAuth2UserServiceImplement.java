package com.speech.up.oAuth.service.implement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.speech.up.oAuth.entity.CustomOAuth2User;
import com.speech.up.user.entity.UserEntity;
import com.speech.up.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImplement extends DefaultOAuth2UserService {
	private final UserRepository userRepository;

	@SuppressWarnings("null")
	@Override
	public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(request);
		String oauthClientName = request.getClientRegistration().getClientName();
		try{
			log.info("check This!! : {}", new ObjectMapper().writeValueAsString(oAuth2User.getAttributes()));
		}catch (Exception exception){
			exception.printStackTrace();
		}
		UserEntity userEntity = null;
		String socialId = null;
		String email = null;
		String name = "";
		String providerType = "";
		String authorization = "GENERAL_USER";
		String level = "bronze";
		if (oauthClientName.equals("kakao")) {
			socialId = oAuth2User.getAttributes().get("id").toString();
			providerType = "KAKAO";
			Map<String, String> responseMap = (Map<String, String>) oAuth2User.getAttributes().get("properties");
			if(responseMap != null){
				name =responseMap.get("nickname");
			}

			userEntity = new UserEntity(socialId, email, level, name, authorization, providerType);
		}
		if (oauthClientName.equals("GitHub")) {
			socialId = oAuth2User.getAttributes().get("id").toString();
			name = oAuth2User.getAttributes().get("name").toString();
			providerType = "GITHUB";
			userEntity = new UserEntity(socialId, email, level, name, authorization, providerType);
		}
		if(oauthClientName.equals("Google")){
			socialId = oAuth2User.getAttributes().get("sub").toString();
			email = oAuth2User.getAttributes().get("email").toString();
			name = oAuth2User.getAttributes().get("name").toString();
			providerType = "GOOGLE";
			userEntity = new UserEntity(socialId, email, level, name, authorization, providerType);
		}

		assert userEntity != null;
		if(!userRepository.existsBySocialId(userEntity.getSocialId())){
			userRepository.save(userEntity);
		}
		return new CustomOAuth2User(socialId);
	}

}
