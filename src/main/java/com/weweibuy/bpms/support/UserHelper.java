package com.weweibuy.bpms.support;

import com.weweibuy.bpms.client.UserClient;
import com.weweibuy.framework.common.core.exception.Exceptions;
import com.weweibuy.framework.common.core.model.dto.CommonDataResponse;
import com.weweibuy.upms.api.user.dto.request.UserQueryReqDTO;
import com.weweibuy.upms.api.user.dto.response.UserRespDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.UserQueryImpl;
import org.camunda.bpm.engine.impl.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author durenhao
 * @date 2020/10/25 11:42
 **/
@Component
@RequiredArgsConstructor
public class UserHelper {

    private final UserClient client;

    private static UserClient userClient;

    @PostConstruct
    public void init() {
        userClient = client;
    }

    public static List<User> queryUser(UserQueryImpl userQuery) {
        return Optional.ofNullable(userClient.queryUser(userQueryToReqDto(userQuery)))
                .map(CommonDataResponse::getData)
                .map(list -> list.stream().map(UserHelper::userRespDTOToUser).collect(Collectors.toList()))
                .orElseThrow(() -> Exceptions.unknown());
    }


    public static Long countUser(UserQueryImpl userQuery) {
        return Optional.ofNullable(userClient.countUser(userQueryToReqDto(userQuery)))
                .map(CommonDataResponse::getData)
                .orElseThrow(() -> Exceptions.unknown());
    }


    private static UserQueryReqDTO userQueryToReqDto(UserQueryImpl userQuery) {
        UserQueryReqDTO userQueryReqDTO = new UserQueryReqDTO();
        userQueryReqDTO.setName(userQuery.getFirstName());
        userQueryReqDTO.setUsername(userQuery.getId());
        Optional.ofNullable(userQuery.getIds())
                .filter(ArrayUtils::isNotEmpty)
                .ifPresent(userQueryReqDTO::setUsernameList);
        userQueryReqDTO.setGroupKey(userQuery.getGroupId());
        Optional.ofNullable(userQuery.getGroupId())
                .ifPresent(userQueryReqDTO::setGroupKey);

        userQueryReqDTO.setEmail(userQuery.getEmail());
        return userQueryReqDTO;
    }

    private static User userRespDTOToUser(UserRespDTO dto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(dto.getUsername());
        userEntity.setEmail(dto.getUsername());
        userEntity.setFirstName(dto.getUsername());
        userEntity.setLastName(dto.getUsername());
        return userEntity;
    }
}
