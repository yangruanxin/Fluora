package org.whu.fleetingtime.util;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyuncs.exceptions.ClientException;

import java.io.InputStream;

public class AliyunOssUtil {

    private static final String ENDPOINT = "https://oss-cn-hangzhou.aliyuncs.com";
    private static final String REGION = "cn-hangzhou";
    private static final String BUCKET_NAME = "fleeting-time";

    private static final OSS ossClient;

    // 静态代码块初始化 OSSClient
    static {
        // 从环境变量中获取访问凭证
        EnvironmentVariableCredentialsProvider credentialsProvider;
        try {
            credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }

        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        conf.setSignatureVersion(SignVersion.V4);

        ossClient = OSSClientBuilder.create()
                .endpoint(ENDPOINT)
                .credentialsProvider(credentialsProvider)
                .region(REGION)
                .build();
    }

    /**
     * 上传文件到指定路径
     *
     * @param objectName OSS 包含路径的对象名，例如 user/10001/avatar.png
     * @param inputStream 文件流
     * @return 公网访问 URL
     */
    public static String upload(String objectName, InputStream inputStream) {
        ossClient.putObject(BUCKET_NAME, objectName, inputStream);
        // 拼接访问 URL（默认公开读的前提下）
        // TODO：后续改成预签名URL
        return "https://" + BUCKET_NAME + ".oss-" + REGION + ".aliyuncs.com/" + objectName;
    }

    /**
     * 删除指定路径的文件
     *
     * @param objectName OSS 包含路径的对象名，例如 user/10001/avatar.png
     */
    public static void delete(String objectName) {
        ossClient.deleteObject(BUCKET_NAME, objectName);
    }

    // 从url获取路径
    public static String extractObjectNameFromUrl(String url) {
        // 例子: https://your-bucket.oss-region.aliyuncs.com/user/10001/avatar.png
        // 提取 user/10001/avatar.png
        int index = url.indexOf(".com/");
        if (index != -1 && index + 5 < url.length()) {
            return url.substring(index + 5);
        }
        return null;
    }

    /**
     * 关闭 OSS 客户端（可选，项目退出时关闭）
     */
    public static void shutdown() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }
}
