package com.bill.test.service;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Slf4j
public class PayService {

    private static final String rsaPublic ="""
            MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAovMpBi8s8NOaqFEfDXZD
            PcN7Rs0jxpPvlw/cA1exwJNEdxNeFA0GyN7JItHJw+ll9Nw1GFgc+fSWqdN2/TEe
            yAT1N6lRlLboZUyuvOjTSf+f8JCb2+03iuo9esBA5HOOYQfluvWBZi6GxrcxRPfC
            f40RQ2ZJ+233STKS1F3J+OH7ePntWvdqjZr/SksOf4fJW0MeNGrjb0U5/RccFu4E
            iyALCJVNsVm15f2Fr+1WIJod0Jm3QFow375YMF89U69f/Nl4h9RKAHrvbESOO/T9
            aVzL1BZsxAnAYYbCXHP52Z/yw/eR3y3I0Ayia3uZgzXOLG7qZ/KWIie/+d/lCvKz
            KwIDAQAB
            """;
    private static final String rsaPrivate= """
            MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCi8ykGLyzw05qo
            UR8NdkM9w3tGzSPGk++XD9wDV7HAk0R3E14UDQbI3ski0cnD6WX03DUYWBz59Jap
            03b9MR7IBPU3qVGUtuhlTK686NNJ/5/wkJvb7TeK6j16wEDkc45hB+W69YFmLobG
            tzFE98J/jRFDZkn7bfdJMpLUXcn44ft4+e1a92qNmv9KSw5/h8lbQx40auNvRTn9
            FxwW7gSLIAsIlU2xWbXl/YWv7VYgmh3QmbdAWjDfvlgwXz1Tr1/82XiH1EoAeu9s
            RI479P1pXMvUFmzECcBhhsJcc/nZn/LD95HfLcjQDKJre5mDNc4sbupn8pYiJ7/5
            3+UK8rMrAgMBAAECggEAK0JfnT8v8J1UnSOwGTxQvJhlZVX3jfPfMtzofVCuJWBT
            WENhgfQ1XWVbw20rYeHYCeneBKD7zFXlG5/CopQERnieexDt0gu+5Ym2h/tNgIMm
            /E//uKKg0LW/7ZMVhzCwV00n7XrYx/5JFH80ysj1rzQNpbWFpYS7lgihl1Rw1qwH
            SyERkpd0ivP6JlJCNgoLpZH0yNF2TohxO3+diZk7IfOhQhiw3W13KNfBO9Urb593
            TCfIBYGhywm2VfKasFB1DJZzQ+bMaJdq/iqQeKGeiAaOLl+diNDeNEnjWO02nPd2
            It5k+QridpX57CJIudnPyEtAOeKozRatEJWcG5XvMQKBgQDNJRs3P/wvgtNgqV2N
            J8D9X3g2jjDVCfJjaE4OJRuLbGuua6Ffj0sNyt3KC4nkbjRM3uAsYRQvZIzT58+Z
            /7orUYjVoKKd82oScoVp/2lplghBr7ybIEvayClTAYpJjzhArLVI6Pd9ENfeE1rB
            ZCd8YD7Rt5vDFdkIEc5obxN9sQKBgQDLWEV+Dsh6+bQ3xEHYTDcKBegqZv14SNdV
            RNMpkv6RO70ToPaQh1uQo9yR+JcZyMdt8Jza530p8SW9d3ou4DsTpohw5mnLMdTF
            NDnrdcJJ6ldtiCalMJ09MjCOOGSjMzFZK3mzLrNuJgF5YfJhN1kZ3HI+Fu8AE4wN
            Evx48xhpmwKBgQCgDEURc9ASWSUEkt4z1EIS6zrkhUa/zsTWnXfYPUciHXJdYAdE
            gIzzs8QZb6sjFn+jQXbv6MVQvEfKw14KLaNXTINO98YnKNMmlUZVDjl1cOH8LVke
            RbIofGURJ3B2N1CR1KySt37uigeAcOUrYDIWLRiVkYPqTc9HJm08uPwh4QKBgQC+
            Gi8Vk5J/zXRbcmIwachG2ZDcG166Fjch428afe76LgQtFp2MLs8+oMKzwNjaZSd0
            s66gxCvyixzCTbFvo3f3fggurGqZaWPGXXYMmBHkcm37235tjs5hiMt2GmiTXmez
            On7TMkTTJDralOQ5WLo3AC3ZuC0GQD/9dRONq47WIwKBgQCFkWoFQF85TN7M5ZAn
            o7MdjrIYzGu9oNrl3W3TjmvRubR6eyFzU5xxbyv8rp4SY7fhEmvXvDO3h1W0UZx/
            99BtmAd1EJoLQNkAand6NS1xsq5RBH07D2RpI8aGvj2M5rCsk2S1iX75M12qPKcz
            J1i5HUp1L5kLc4V/orTItM6RSw==
            """;
    private static final String  aesKey="5nbnkF4c+srMHHKaJvLDxQ==";


    private String enRsa(String value){
        Sign sign = new Sign(SignAlgorithm.SHA256withRSA, rsaPrivate.trim(), null);
        // 生成签名
        byte[] signBytes = sign.sign(value.getBytes(StandardCharsets.UTF_8));
        // Base64 输出
        return Base64.encode(signBytes);
    }

    private String deRsa(String value){
        RSA rsa = new RSA(null, rsaPublic.trim());
        // 公钥解密
        byte[] decryptedBytes = rsa.decrypt(value, KeyType.PublicKey);
        return new String(decryptedBytes);
    }

    @SneakyThrows
    private String enAes(String value){
        // 1. 生成随机 IV
        byte[] iv = new byte[12];
        // 2. 构造 Cipher
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(Base64.decode(aesKey), "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

        // 3. 加密
        byte[] cipherText = cipher.doFinal(
                value.getBytes(StandardCharsets.UTF_8)
        );

        // 4. iv + cipherText 拼接
        byte[] result = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(cipherText, 0, result, iv.length, cipherText.length);

        // 5. Base64 输出
        return Base64.encode(result);
    }

    @SneakyThrows
    private String deAes(String value){

        // 1. Base64 解码（确保和加密方一致）
        byte[] allBytes = java.util.Base64.getDecoder().decode(value);

        // 2. 拆 IV（12 bytes）和 密文+Tag
        byte[] iv = new byte[12];
        byte[] cipherText = new byte[allBytes.length - 12];

        System.arraycopy(allBytes, 0, iv, 0, 12);
        System.arraycopy(allBytes, 12, cipherText, 0, cipherText.length);

        // 3. Cipher
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        //  确保 aesKey 是 Base64 的，这里 decode 一次即可
        byte[] keyBytes = java.util.Base64.getDecoder().decode(aesKey);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);

        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

        // 4. 解密（这里会校验 Tag）
        byte[] plainText = cipher.doFinal(cipherText);

        return new String(plainText, StandardCharsets.UTF_8);
    }

    public String payIn(){
        String merchantId="2507163300001";
        String version="2.0";
        String requestId=IdUtil.fastSimpleUUID();
        String timestamp=System.currentTimeMillis()+"";

        HttpRequest request= HttpUtil.createPost("http://10.0.159.4:9012/trade/pay_in");
        request.header("Content-Type","application/json;charset=utf-8");
        request.header("x-merchant-id",merchantId);
        request.header("x-version",version);
        request.header("x-request-id", requestId);
        request.header("x-timestamp",timestamp);

        JSONObject body = new JSONObject();
        body.set("mchId", merchantId);
        body.set("notifyUrl", "http://localhost/in/resp");
        body.set("outTradeNo", "9abe9cbfda7348728cb2364e6c4c9b40");
        body.set("payerIp", "127.0.0.1");
        body.set("userId", "10001");
        body.set("successfulTopUpTimes", 0);
        body.set("phoneNumber", "91223211223");
        body.set("email", "sample@gmail.com");
        body.set("tradeAmount", 1);

        log.info("body:{}",JSONUtil.toJsonStr(body));
        String payload = enAes(JSONUtil.toJsonStr(body));
        log.info("payload:{}",payload);
        String signStr = String.join("|",merchantId,version,requestId,timestamp,payload);

        log.info("signStr: {} ", signStr);
        String sign=enRsa(signStr);
        log.info("sign: {} ", sign);
        request.header("x-sign",sign);

        JSONObject payloadBody=new JSONObject();
        payloadBody.set("payload",payload);

        request.body(JSONUtil.toJsonStr(payloadBody));
        log.info("request:{}",request);
        HttpResponse response= request.execute();
        log.info("response:{}",response);
        log.info("response body:{}",response.body());

        return response.body();

    }

    public static void main(String[] args) throws Exception {
       PayService payService = new PayService();
       String body= payService.payIn();
       String payload=JSONUtil.parseObj(body).getStr("payload");
       log.info("payload:{}",payload);
       String json=payService.deAes(payload);

       log.info("json:{}",json);

      /* String s1=  payService.enRsa("123");
        System.out.println(s1);
        String s2=  payService.deRsa(s1);
        System.out.println(s2);*/

    }


}
