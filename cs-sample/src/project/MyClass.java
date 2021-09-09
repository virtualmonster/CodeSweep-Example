/************************************************************************
* Licensed Materials - Property of IBM and/or HCL
* (c) Copyright HCL Technologies Ltd. 2021.  All Rights Reserved.
*
* Note to U.S. Government Users Restricted Rights:
* Use, duplication or disclosure restricted by GSA ADP Schedule
* Contract with IBM Corp.
*************************************************************************/

/**
 * 
 * @author JP
 */
public class MyClass {
  public static void main(String[] args) {
    System.out.println("Hello World");


    Intent intent = (getSystemService(Context.STORAGE_SERVICE) as StorageManager) .primaryStorageVolume.createAccessIntent() startActivityForResult(intent, PICTURES_DIR_ACCESS_REQUEST_CODE)
    
    KeyGenerator keygen = KeyGenerator.getInstance("DES") Cipher cipher = Cipher.getInstance("DES/ECB/PKCS7Padding", "BC") cipher.init(Cipher.ENCRYPT_MODE, keygen.generateKey())


  }

}
