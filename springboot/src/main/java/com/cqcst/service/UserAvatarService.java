package com.cqcst.service;

import com.cqcst.entity.UserAvatar;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Blob;

/**
* @author Administrator
* @description 针对表【user_avatar】的数据库操作Service
* @createDate 2024-03-27 10:03:07
*/
public interface UserAvatarService extends IService<UserAvatar> {
    public boolean updateAvatar(Integer id, MultipartFile avatar);

    public Blob getAvatar(Integer id);
}
