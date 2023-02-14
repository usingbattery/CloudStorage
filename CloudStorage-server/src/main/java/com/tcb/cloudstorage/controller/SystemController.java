package com.tcb.cloudstorage.controller;

import com.alibaba.fastjson.JSON;
import com.tcb.cloudstorage.domain.FileStoreInfo;
import com.tcb.cloudstorage.domain.Folder;
import com.tcb.cloudstorage.domain.User;
import com.tcb.cloudstorage.domain.UserFile;
import com.tcb.cloudstorage.mapper.FileStoreMapper;
import com.tcb.cloudstorage.service.FileService;
import com.tcb.cloudstorage.service.FileStoreService;
import com.tcb.cloudstorage.service.FolderService;
import com.tcb.cloudstorage.utils.JedisUtils;
import com.tcb.cloudstorage.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
/*
* 系统页面跳转控制器
* */
@RestController
@RequestMapping("/index")
public class SystemController extends BaseController
{
    @Autowired
    private FolderService folderService;

    @Autowired
    private FileStoreService fileStoreService;

    @Autowired
    private FileService fileService;

    @RequestMapping("/file-list")
    public R getFileList(Integer folderId)
    {

        //包含的子文件夹
        List<Folder> folders = null;
        //包含的文件
        List<UserFile> files = null;
        //当前文件夹信息
        Folder nowFolder = null;
        //当前文件夹的相对路径
        List<Folder> route = new ArrayList<>();
        if (folderId == null || folderId <= 0) {
            //代表当前为根目录
            folderId = 0;
            folders = folderService.getRootFolderByFileStoreId(loginUser.getFileStoreId());
            files = fileService.getRootFileByFileStoreId(loginUser.getFileStoreId());
            nowFolder = Folder.builder().folderId(folderId).fileStoreId(loginUser.getFileStoreId()).build();
        } else {
            //当前为具体目录,访问的文件夹不是当前登录用户所创建的文件夹
            Folder folder = folderService.getFolderById(folderId);
            if (folder.getFileStoreId() - loginUser.getFileStoreId() != 0){
                return new R(false,"无访问权限");
            }
            //当前为具体目录，访问的文件夹是当前登录用户所创建的文件夹
            folders = folderService.getFolderByParentFolderId(folderId);
            files = fileService.getUserFileByParentFolderId(folderId);
            nowFolder = folderService.getFolderById(folderId);
            //遍历查询当前目录
            Folder temp = nowFolder;
            //获取中间路径
            while (temp.getParentFolderId() != 0) {
                temp = folderService.getFolderById(temp.getParentFolderId());
                route.add(temp);
            }
        }
        //反转获得导航目录
        Collections.reverse(route);
        //获得统计信息
        FileStoreInfo fileStoreInfo = fileStoreService.getFileStoreInfo(loginUser.getFileStoreId());
        List<Object> foldersAndFiles = new ArrayList<>(folders);
        for (int i = 0; i < files.size(); i++)
            foldersAndFiles.add(files.get(i));
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("fileStoreInfo", fileStoreInfo);
        map.put("datalist", foldersAndFiles);
        map.put("nowFolder", nowFolder);
        map.put("location", route);

        return new R(true, "文件列表加载成功", map);
    }
}