package com.deals.server.repository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Repository
public class S3Repository {

    @Autowired
    private AmazonS3 s3Client;

    public String storeImage(byte[] imageBytes, String key){
        ObjectMetadata objMetaData = new ObjectMetadata();
        objMetaData.setContentType("image/png");
        objMetaData.setContentLength(imageBytes.length);
        String imageUrl = "";
        InputStream imageIS = new ByteArrayInputStream(imageBytes);
        try {
            PutObjectRequest putObjReq = new PutObjectRequest("gd-deals-bucket", key, imageIS, objMetaData);
            putObjReq.withCannedAcl(CannedAccessControlList.PublicRead);
            s3Client.putObject(putObjReq);
            imageUrl = "https://gd-deals-bucket.sgp1.digitaloceanspaces.com/%s".formatted(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageUrl;
    } 

    public boolean imageExists(String uuid){
        return s3Client.doesObjectExist("gd-deals-bucket", uuid);
    }
}
