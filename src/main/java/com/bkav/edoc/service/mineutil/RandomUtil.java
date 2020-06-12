/**
 * 
 */
package com.bkav.edoc.service.mineutil;

import java.util.Random;

import com.bkav.edoc.service.resource.StringPool;


/*QuyenDN - Oct 11, 2013*/

/**
 * @author QuyenDN
 *
 */
public class RandomUtil {
	
	/**
	 * @return
	 */
	static public String randomId(){
		char[] chars = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
		  
		  StringBuilder sb = new StringBuilder(StringPool.BLANK);
		  
		  Random random = new Random();
		  
		  for (int i = 0; i < 10; i++) {
		      char c = chars[random.nextInt(chars.length)];		      
		      sb.append(c);
		  }
		  return sb.toString();
	}
}

