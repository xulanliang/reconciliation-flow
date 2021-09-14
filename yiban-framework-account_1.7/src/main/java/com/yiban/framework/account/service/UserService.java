package com.yiban.framework.account.service;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springside.modules.persistence.SearchFilter;

import com.yiban.framework.account.domain.User;

/**
 * 用户管理
 * @author tantian
 * @date 2018-01-23
 */
public interface UserService {

    /**
     * 根据用户名查询用户列表
     * @param searchFilters
     * @param pageable
     * @return
     */
    Page<User> findList(Collection<SearchFilter> searchFilters, Pageable pageable);

    /**
     * 新增用户
     * @param user
     * @return
     */
    void saveUser(User user);

    /**
     * 修改用户
     * @param user
     */
    void updateUser(User user);

    /**
     * 删除用户
     * @param user
     */
    void deleteUser(User user);
    
    public User queryUserByUsername(String username);

    /**
     * 分页查询条件类
     */
    class userQuery{
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
