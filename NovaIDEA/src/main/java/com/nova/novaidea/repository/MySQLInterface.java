package com.nova.novaidea.repository;

import com.nova.novaidea.bean.Machine;
import com.nova.novaidea.bean.User;
import com.nova.novaidea.bean.UserMachine;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface  MySQLInterface extends JpaRepository<User,Long>{
    //根据用户名和密码查找用户
    @Query("select user from User user where user.userName = :userName and user.password=:password ")
    public List<User> findPerson(@Param("userName") String userName,@Param("password") String password);//@Param("geoCode")

    //根据用户id查找该用户所拥有的所有机器的id
    @Query("select machine from Machine  machine where  machine.id in(select userMachine.machineid from UserMachine userMachine,User user where user.id=userMachine.userid and userMachine.userid = :userid) ")
    public List<Machine> findUserMachine(@Param("userid") Integer userid);
}
