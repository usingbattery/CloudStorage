package com.tcb.cloudstorage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcb.cloudstorage.domain.FileStoreInfo;
import com.tcb.cloudstorage.domain.User;
import com.tcb.cloudstorage.domain.UserFile;
import com.tcb.cloudstorage.mapper.FileMapper;
import com.tcb.cloudstorage.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, UserFile> implements FileService
{
    @Autowired
    private FileMapper fileMapper;

    /**
     * @Description 添加文件
     * @param userFile
     * @return boolean
     */
    @Override
    public boolean addUserFile(UserFile userFile)
    {
        return fileMapper.insert(userFile)>0;
    }

    @Override
    public boolean updateUserFileById(UserFile userFile)
    {
        return fileMapper.updateById(userFile)>0;
    }

    /**
     * @Description 根据文件夹id获取文件夹下的文件
     * @param folderId
     * @return
     */
    @Override
    public List<UserFile> getUserFileByParentFolderId(int folderId)
    {
        return fileMapper.getUserFileByFolderId(folderId);
    }

    /**
     * @Description 根据文件仓库id获取根目录下的文件
     * @param fileStoreId
     * @return
     */
    @Override
    public List<UserFile> getRootFileByFileStoreId(int fileStoreId)
    {
        return fileMapper.getRootFileByFileStoreId(fileStoreId);
    }


    /**
     * @Description 根据文件id获取文件
     * @param fileId
     * @return
     */
    @Override
    public UserFile getFileByFileId(int fileId)
    {
        return fileMapper.selectById(fileId);
    }

    @Override
    public boolean deleteFileById(int fileId)
    {
        return fileMapper.deleteById(fileId)>0;
    }

    /**
     * @Description 根据文件的后缀名获得对应的类型
     * @Author xw
     * @Date 23:20 2020/2/10
     * @Param [type]
     * @return int 1:文本类型   2:图像类型  3:视频类型  4:音乐类型  5:其他类型
     **/
    @Override
    public int getFileTypeByPostfix(String type){
        //1-doc
        if (".chm".equals(type)||".txt".equals(type)||".xmind".equals(type)||".xlsx".equals(type)||".md".equals(type)
                ||".doc".equals(type)||".docx".equals(type)||".pptx".equals(type)
                ||".wps".equals(type)||".word".equals(type)||".html".equals(type)||".pdf".equals(type)){
            return  1;
        }
        //2-image
        else if (".bmp".equals(type)||".gif".equals(type)||".jpg".equals(type)||".ico".equals(type)||".vsd".equals(type)
                ||".pic".equals(type)||".png".equals(type)||".jepg".equals(type)||".jpeg".equals(type)||".webp".equals(type)
                ||".svg".equals(type)){
            return 2;
        }
        //3-video
        else if (".avi".equals(type)||".mov".equals(type)||".qt".equals(type)
                ||".asf".equals(type)||".rm".equals(type)||".navi".equals(type)||".wav".equals(type)
                ||".mp4".equals(type)||".mkv".equals(type)||".webm".equals(type)){
            return 3;
        }
        //4-music
        else if (".mp3".equals(type)||".wma".equals(type)){
            return 4;
        }
        //5-other
        else {
            return 5;
        }
    }
    /**
     * @Description 正则验证文件名是否合法 [汉字,字符,数字,下划线,英文句号,横线]
     * @Author xw
     * @Date 23:22 2020/2/10
     * @Param [target]
     * @return boolean
     **/
    public boolean checkFileName(String target) {
        final String format = "[^\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w-_.]";
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(target);
        return !matcher.find();
    }

    @Override
    public boolean isFileRepeat(int parentFolderId, String fileName, String postfix)
    {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new QueryWrapper<UserFile>().lambda();
        lambdaQueryWrapper.eq(UserFile::getParentFolderId, parentFolderId);
        lambdaQueryWrapper.eq(UserFile::getFileName, fileName);
        lambdaQueryWrapper.eq(UserFile::getPostfix, postfix);
        return fileMapper.selectCount(lambdaQueryWrapper)>0;
    }

}