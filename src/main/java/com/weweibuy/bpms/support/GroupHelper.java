package com.weweibuy.bpms.support;

import com.weweibuy.bpms.client.GroupClient;
import com.weweibuy.framework.common.core.exception.Exceptions;
import com.weweibuy.framework.common.core.model.dto.CommonDataResponse;
import com.weweibuy.upms.api.user.dto.request.GroupQueryReqDTO;
import com.weweibuy.upms.api.user.dto.response.GroupRespDTO;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.impl.GroupQueryImpl;
import org.camunda.bpm.engine.impl.persistence.entity.GroupEntity;
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
public class GroupHelper {

    private final GroupClient client;

    private static GroupClient groupClient;

    @PostConstruct
    public void init() {
        groupClient = client;
    }

    public static List<Group> queryGroup(GroupQueryImpl userQuery) {
        return Optional.ofNullable(groupClient.queryGroup(groupQueryToReqDto(userQuery)))
                .map(CommonDataResponse::getData)
                .map(list -> list.stream().map(GroupHelper::userRespDTOToUser).collect(Collectors.toList()))
                .orElseThrow(() -> Exceptions.unknown());
    }


    public static Long countGroup(GroupQueryImpl userQuery) {
        return Optional.ofNullable(groupClient.countGroup(groupQueryToReqDto(userQuery)))
                .map(CommonDataResponse::getData)
                .orElseThrow(() -> Exceptions.unknown());
    }


    private static GroupQueryReqDTO groupQueryToReqDto(GroupQueryImpl groupQuery) {
        GroupQueryReqDTO userQueryReqDTO = new GroupQueryReqDTO();
        userQueryReqDTO.setName(groupQuery.getName());
        userQueryReqDTO.setUserName(groupQuery.getUserId());

        Optional.ofNullable(groupQuery.getIds())
                .ifPresent(userQueryReqDTO::setUsernameList);

        userQueryReqDTO.setGroupKey(groupQuery.getId());

        Optional.ofNullable(groupQuery.getIds())
                .ifPresent(userQueryReqDTO::setGroupKeyList);

        userQueryReqDTO.setNameLike(groupQuery.getNameLike());
        return userQueryReqDTO;
    }

    private static Group userRespDTOToUser(GroupRespDTO dto) {
        GroupEntity groupEntity = new GroupEntity();
        groupEntity.setId(dto.getGroupCode());
        groupEntity.setName(dto.getGroupName());
        return groupEntity;
    }
}
