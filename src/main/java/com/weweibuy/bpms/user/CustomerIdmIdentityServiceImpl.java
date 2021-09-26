package com.weweibuy.bpms.user;

import com.weweibuy.framework.common.core.exception.Exceptions;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.identity.*;
import org.camunda.bpm.engine.impl.identity.Account;
import org.camunda.bpm.engine.impl.identity.Authentication;

import java.util.List;
import java.util.Map;

/**
 * @author durenhao
 * @date 2020/10/23 22:17
 **/
public class CustomerIdmIdentityServiceImpl implements IdentityService {

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public User newUser(String userId) {
        throw Exceptions.business("不支持用户操作相关功能");
    }

    @Override
    public void saveUser(User user) {
        throw Exceptions.business("不支持用户操作相关功能");
    }


    @Override
    public UserQuery createUserQuery() {
        return new CustomUserQuery();
    }

    @Override
    public NativeUserQuery createNativeUserQuery() {
        return new CustomUserQuery();
    }

    @Override
    public void deleteUser(String userId) {
        throw Exceptions.business("不支持用户操作相关功能");
    }

    @Override
    public void unlockUser(String userId) {

    }

    @Override
    public Group newGroup(String groupId) {
        throw Exceptions.business("不支持用户操作相关功能");
    }

    @Override
    public GroupQuery createGroupQuery() {
        return new CustomGroupQuery();
    }


    @Override
    public void saveGroup(Group group) {
        throw Exceptions.business("不支持用户操作相关功能");
    }

    @Override
    public void deleteGroup(String groupId) {
        throw Exceptions.business("不支持用户操作相关功能");
    }

    @Override
    public void createMembership(String userId, String groupId) {
        throw Exceptions.business("不支持用户操作相关功能");
    }

    @Override
    public void deleteMembership(String userId, String groupId) {
        throw Exceptions.business("不支持用户操作相关功能");
    }

    @Override
    public Tenant newTenant(String tenantId) {
        return null;
    }

    @Override
    public TenantQuery createTenantQuery() {
        return null;
    }

    @Override
    public void saveTenant(Tenant tenant) {

    }

    @Override
    public void deleteTenant(String tenantId) {

    }

    @Override
    public void createTenantUserMembership(String tenantId, String userId) {

    }

    @Override
    public void createTenantGroupMembership(String tenantId, String groupId) {

    }

    @Override
    public void deleteTenantUserMembership(String tenantId, String userId) {

    }

    @Override
    public void deleteTenantGroupMembership(String tenantId, String groupId) {

    }

    @Override
    public boolean checkPassword(String userId, String password) {
        throw Exceptions.business("不支持用户操作相关功能");
    }

    @Override
    public PasswordPolicyResult checkPasswordAgainstPolicy(String password) {
        return null;
    }

    @Override
    public PasswordPolicyResult checkPasswordAgainstPolicy(String candidatePassword, User user) {
        return null;
    }

    @Override
    public PasswordPolicyResult checkPasswordAgainstPolicy(PasswordPolicy policy, String password) {
        return null;
    }

    @Override
    public PasswordPolicyResult checkPasswordAgainstPolicy(PasswordPolicy policy, String candidatePassword, User user) {
        return null;
    }

    @Override
    public PasswordPolicy getPasswordPolicy() {
        return null;
    }

    @Override
    public void setAuthenticatedUserId(String authenticatedUserId) {
        throw Exceptions.business("不支持用户操作相关功能");
    }

    @Override
    public void setAuthentication(String userId, List<String> groups) {

    }

    @Override
    public void setAuthentication(String userId, List<String> groups, List<String> tenantIds) {

    }

    @Override
    public void setAuthentication(Authentication currentAuthentication) {

    }

    @Override
    public Authentication getCurrentAuthentication() {
        return null;
    }

    @Override
    public void clearAuthentication() {

    }

    @Override
    public void setUserPicture(String userId, Picture picture) {
        throw Exceptions.business("不支持用户操作相关功能");
    }

    @Override
    public Picture getUserPicture(String userId) {
        return null;
    }

    @Override
    public void deleteUserPicture(String userId) {

    }


    @Override
    public void setUserInfo(String userId, String key, String value) {
        throw Exceptions.business("不支持用户操作相关功能");
    }

    @Override
    public String getUserInfo(String userId, String key) {
        return null;
    }

    @Override
    public List<String> getUserInfoKeys(String userId) {
        return null;
    }

    @Override
    public void deleteUserInfo(String userId, String key) {
        throw Exceptions.business("不支持用户操作相关功能");
    }

    @Override
    public void setUserAccount(String userId, String userPassword, String accountName, String accountUsername, String accountPassword, Map<String, String> accountDetails) {

    }

    @Override
    public List<String> getUserAccountNames(String userId) {
        return null;
    }

    @Override
    public Account getUserAccount(String userId, String userPassword, String accountName) {
        return null;
    }

    @Override
    public void deleteUserAccount(String userId, String accountName) {

    }


    private void notSupportFunc() {
        throw Exceptions.business("不支持用户操作相关功能");
    }
}
