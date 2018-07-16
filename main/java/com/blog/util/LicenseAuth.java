package com.blog.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;
import sun.plugin2.message.Message;

import java.io.*;
import java.net.NetworkInterface;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by paul on 2018/7/12.
 */
public class LicenseAuth {
    private static final int SPLITLENGTH = 4;
    public static String getMachineCode() throws Exception{
        Set<String> result = new HashSet<>();
        String mac = getMac();
        result.add(mac);
        Properties props = System.getProperties();
        String javaVersion = props.getProperty("java.version");
        result.add(javaVersion);
        String javaVMVersion = props.getProperty("java.vm.version");
        result.add(javaVMVersion);
        String osVersion = props.getProperty("os.version");
        result.add(osVersion);
        String code = Encrpt.GetMD5Code(result.toString());
        return getSplitString(code, "-", 4);

    }

    public static String auth(String machineCode) throws Exception{
        String newCode = "(yanpeng19940119@gmail.com)["
                + machineCode.toUpperCase() + "](YBLOG开发平台)";
        String code =  Encrpt.GetMD5Code(newCode)
                .toUpperCase() + machineCode.length();
        return getSplitString(code);
    }

    private static HashMap<String, String> genDataFromArrayByte(byte[] b) throws IOException {
        BufferedReader br=new BufferedReader(new InputStreamReader(new ByteArrayInputStream(b)));
        HashMap<String, String> data = new HashMap<String, String>();
        String str =  null;
        while((str = br.readLine()) != null){
            if(StringUtils.isNotEmpty(str)){
                str = str.trim();
                int pos = str.indexOf("=");
                if(pos <= 0 ) continue;
                if(str.length() > pos + 1){
                    data.put(str.substring(0, pos).trim().toUpperCase(), str.substring( pos + 1).trim()) ;
                }else{
                    data.put(str.substring(0, pos).trim().toUpperCase(), "") ;
                }
            }
        }
        return data;
    }

    private static String getSplitString(String str) {
        return getSplitString(str, "-", SPLITLENGTH);
    }

    private static String getSplitString(String str, String split, int length) {
        int len = str.length();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < len; i++) {
            if (i % length == 0 && i > 0) {
                temp.append(split);
            }
            temp.append(str.charAt(i));
        }
        String[] attrs = temp.toString().split(split);
        StringBuilder finalMachineCode = new StringBuilder();
        for (String attr : attrs) {
            if (attr.length() == length) {
                finalMachineCode.append(attr).append(split);
            }
        }
        String result = finalMachineCode.toString().substring(0,
                finalMachineCode.toString().length() - 1);
        return result;
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    private static String getMac() {
        try {
            Enumeration<NetworkInterface> el = NetworkInterface
                    .getNetworkInterfaces();
            while (el.hasMoreElements()) {
                byte[] mac = el.nextElement().getHardwareAddress();
                if (mac == null)
                    continue;
                String hexstr = bytesToHexString(mac);
                return getSplitString(hexstr, "-", 2).toUpperCase();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static void getLicense(String isNoTimeLimit, String licenseLimit, String machineCode, String licensePath, String priavateKeyPath) throws Exception{
        String[] liccontent = {
                "LICENSEID=yanpeng19940119@gmail.com",
                "LICENSENAME=YBLOG使用证书",
                MessageFormat.format("LICENSETYPE={0}",isNoTimeLimit),
                MessageFormat.format("EXPIREDAY={0}",licenseLimit), //日期采用yyyy-MM-dd日期格式
                MessageFormat.format("MACHINECODE={0}",machineCode),
                ""
        };

        //将lic内容进行混合签名并写入内容
        StringBuilder sign = new StringBuilder();
        for(String item:liccontent){
            sign.append(item+"yblog");
        }
        liccontent[5] = MessageFormat.format("LICENSESIGN={0}",Encrpt.GetMD5Code(sign.toString()));
        FileUtil.createFileAndWriteLines(licensePath,liccontent);
        //将写入的内容整体加密替换
        String filecontent =FileUtil.readFileToString(licensePath);
        String encrptfilecontent = Encrpt.EncriptWRSA_Pri(filecontent,priavateKeyPath);
        File file = new File(licensePath);
        file.delete();
        FileUtil.createFile(licensePath,encrptfilecontent);
    }

    public static boolean authLicense() throws Exception{
        boolean isauth = false;
        String pubkpath = ResourceUtils.getURL("src/main/resources/static/lic/").getPath()+"yblog.crt";
        String licpath = ResourceUtils.getURL("src/main/resources/static/lic/").getPath();
        File lic = new File(licpath);
        String[] filelist = lic.list();
        if (filelist.length>0){
            for (int i = 0; i < filelist.length; i++) {
                if (filelist[i].contains(".lic")){
                    File readfile = new File(licpath + filelist[i]);
                    if (readfile.isFile()) {
                        String liccontent = FileUtil.readFileToString(readfile);
                        String decriptliccontent = Encrpt.DecriptWithRSA_Pub(liccontent,pubkpath);
                        HashMap<String, String> props = genDataFromArrayByte(decriptliccontent.getBytes());
                        String licenseid = props.get("LICENSEID");
                        String licensename= props.get("LICENSENAME");
                        String licensetype = props.get("LICENSETYPE");
                        String liclimit = props.get("EXPIREDAY");
                        String machinecode = props.get("MACHINECODE");
                        String lincensesign = props.get("LICENSESIGN");
                        //验证签名
                        String allinfogroup = "LICENSEID="+licenseid+"yblog"+"LICENSENAME="+licensename+"yblog"+
                                "LICENSETYPE="+licensetype+"yblog"+"EXPIREDAY="+liclimit+"yblog"+"MACHINECODE="+machinecode+"yblogyblog";
                        if (lincensesign.equals(Encrpt.GetMD5Code(allinfogroup))){
                            //验证机器码
                            if (getMachineCode().equals(machinecode)){
                                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                                Date bt=new Date();
                                Date et=sdf.parse(liclimit);
                                //验证时间
                                if(bt.compareTo(et)<=0){
                                    isauth = true;
                                    System.out.println("注册文件:"+filelist[i]+",已通过验证");
                                    break;
                                }else{
                                    System.out.println("证书过期");
                                }
                            }else{
                                System.out.println("机器码不一致");
                            }
                        }else{
                            System.out.println("签名不一致");
                        }
                    }
                }
            }
        }else{
            System.out.println("未上传证书");
        }
        return isauth;
    }
}
