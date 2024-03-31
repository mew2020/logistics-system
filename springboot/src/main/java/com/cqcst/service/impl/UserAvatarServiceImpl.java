package com.cqcst.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqcst.entity.UserAvatar;
import com.cqcst.mapper.UserAvatarMapper;
import com.cqcst.service.UserAvatarService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

/**
* @author Administrator
* @description 针对表【user_avatar】的数据库操作Service实现
* @createDate 2024-03-27 10:03:07
*/
@Service
public class UserAvatarServiceImpl extends ServiceImpl<UserAvatarMapper, UserAvatar>
    implements UserAvatarService{
    @Override
    public boolean updateAvatar(Integer id, MultipartFile avatar) {
        try {
            UserAvatar userAvatar = new UserAvatar();
            userAvatar.setId(id);
            userAvatar.setAvatar(avatar.getBytes());
            return saveOrUpdate(userAvatar);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Blob getAvatar(Integer id) {
        UserAvatar userAvatar = getById(id);
        if(userAvatar == null) return null;

        try {
            byte[] avatar = userAvatar.getAvatar();
            return avatar == null ? null : new SerialBlob(avatar);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}




