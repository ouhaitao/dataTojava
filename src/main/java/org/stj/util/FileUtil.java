package org.stj.util;

import org.stj.exception.FileException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author godBless 2022/05/23
 */
public class FileUtil {
    
    public static boolean writeFile(String path, String content) {
        File file = new File(path);
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            LogUtil.error(new FileException("创建文件路径失败"));
            return false;
        }
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    LogUtil.error(new FileException("创建" + file.getPath() + "失败"));
                    return false;
                }
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(content);
                writer.flush();
            } catch (IOException e) {
                LogUtil.error("写入文件失败", e);
                return false;
            }
        } else {
            LogUtil.error(new FileException(file.getPath() + "已存在"));
            return false;
        }
        return true;
    }
    
//    public static boolean writeFile(String path, String content) {
//        File file = new File(path);
//        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
//            LogUtil.info("创建文件路径失败");
//            return false;
//        }
//        file.deleteOnExit();
//        try {
////            if (!file.createNewFile()) {
////                LogUtil.log("创建文件失败");
////                return false;
////            }
//            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
//            writer.write(content);
//            writer.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
    
    private FileUtil(){}
}
