package org.whu.fleetingtime.util;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

public class AliyunOssUtilTest {

    @Test
    @Order(1)
    public void testUpload() {
        String userId = "testUser";
        String fileName = UUID.randomUUID() + ".txt";
        String objectPath = "user/" + userId + "/" + fileName;

        String content = "这是一段测试上传到 OSS 的内容";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());

        /**
         * 测试上传文件到阿里云 OSS
         */
        String url = AliyunOssUtils.upload(objectPath, inputStream);
        System.out.println("上传成功！访问链接: " + url);
    }

    /**
     * 测试从阿里云 OSS 删除文件
     */
    @Test
    @Order(2)
    public void testDelete() {
        String path = "user/testUser/4518aba8-f0d7-4684-adc5-a0560fff2f01.txt";
        AliyunOssUtils.delete(path);
        System.out.println("删除成功：" + path);
    }
}
