package com.weweibuy.bpms.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @date 2025/4/24
 **/
@Slf4j
@Component("userQueryService")
@RequiredArgsConstructor
public class UserQueryService {


    public String findOneUser() {
        return "james";
    }

    public List<String> queryKey(String key) {
        List<String> userList = new ArrayList<>();
        userList.add("张三");
        userList.add("李四");
        userList.add("王五");
        return userList;

    }


}
