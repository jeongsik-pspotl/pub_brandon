package com.pspotl.sidebranden.manager.util.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import websquare.util.BASE64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class SimpleSymmetricWebSquareLicense extends AbstractWebSquareLicense {

    /**
     * 라이센스 유효 여부.
     */
    private static boolean validity = false;

    /**
     * 라이센스 정보를 담은 문자열.
     */
    private static String licenseStatusString = "";

    public static boolean status() {
        return validity;
    }

    private static final Logger logger = LoggerFactory.getLogger(SimpleSymmetricWebSquareLicense.class);

    /**
     * 라이선스에 대한 컨스트럭트. 암호화된 스트링이 BASE64를 사용하여 인코드되었다고 가정
     * r
     * @throws InvalidLicenseException 라이선스 키가 부적절할 경우
     */
    public SimpleSymmetricWebSquareLicense(String encLicenseString) throws InvalidLicenseException {
        super(encLicenseString);
        try{
            licenseStatusString = loadData(encLicenseString);
        }catch(Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    @Override
    protected String decipherLicense(String origLicense) throws InvalidLicenseException {
        String retval;
        try {

            //	DOS와 UNIX 차이를 보정한다.
            //	\n => \r\n으로 바꾼다.
            //	\r => \r\n으로 바꾼다.
            StringBuffer sb = new StringBuffer();
            char[] charArray = origLicense.toCharArray();
            for (int i = 0; i < charArray.length; i++) {
                if (charArray[i] == '\r') {
                    sb.append('\r');
                    if (i == charArray.length - 1) {		//	마지막 index
                        sb.append('\n');
                    } else {
                        if (charArray[i+1] == '\n') {
                            sb.append('\n');
                            i++;
                        } else {
                            sb.append('\n');
                        }
                    }

                } else if (charArray[i] == '\n') {

                    if (i == 0) {
                        sb.append('\r');
                        sb.append('\n');
                    } else {
                        if (charArray[i-1] == '\r') {
                            sb.append('\r');
                        } else {
                            sb.append('\r');
                            sb.append('\n');
                        }
                    }
                } else {
                    sb.append(charArray[i]);
                }
            }
            origLicense = sb.toString();
            //	키 추출
            String mySecretKeyBASE64 = origLicense.substring(5, 35).trim();
            origLicense = origLicense.substring(0, 5) + origLicense.substring(35);

            byte[] decodedKey = BASE64.decode(mySecretKeyBASE64);
            byte[] ciphertext = BASE64.decode(origLicense);
            byte[] cleartext = null;

            byte[] decodedKeyreal = new byte[8];
            System.arraycopy(decodedKey, 0, decodedKeyreal, 0, 8);

            String VM = System.getProperty("java.vm.vendor");
            java.security.Provider provider = null;
            if( VM.startsWith( "IBM" ) ) {	//
                provider = java.security.Security.getProvider("IBMJCE");
            } else {
                provider = java.security.Security.getProvider("SunJCE");
            }


            //	스태틱 데이터에서 DES 키 생성
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DES", provider);

            SecretKey myKey = skf.generateSecret(new DESKeySpec(decodedKeyreal));

            Cipher cipher = Cipher.getInstance("DES", provider);
            cipher.init(Cipher.DECRYPT_MODE, myKey);

            //	암호문 해독
            cleartext = cipher.doFinal(ciphertext);
            retval = new String(cleartext);

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new InvalidLicenseException("Decryption of license key failed");
        }
        return retval;
    }

}
