package org.kleric.happyfuntime;

import java.math.BigInteger; 
import java.util.HashMap; 
/** 
 *  
 * @author Rahul 
 *  
 */
public class Shamir { 
  
    private final BigInteger prime; 
  
    // make the integer into a big integer 
    private static BigInteger big(int integer) { 
        return new BigInteger(Integer.toString(integer)); 
    } 
  
    public Shamir(BigInteger prime) { 
        this.prime = prime; 
    } 
  
    /** 
     * @return a random number between 0 and 2*prime 
     */
    private BigInteger random() { 
        return prime.multiply(big(2).multiply( 
                new BigInteger(Double.toString(Math.random())))); 
    } 
  
    /** 
     * @param secret 
     *            The secret to be split 
     * @param numberOfShares 
     *            Number of shares to split into 
     * @param necessaryShares 
     *            Number of shares necessary to decode the secret 
     * @return The shares 
     */
    public HashMap<Integer, BigInteger> split(BigInteger secret, 
            int numberOfShares, int necessaryShares) { 
        HashMap<Integer, BigInteger> shares = new HashMap<Integer, BigInteger>(); 
        BigInteger value = big(0); 
  
        for (int x = 1; x <= numberOfShares; x++) { 
            value = secret; 
            for (int power = 1; power < necessaryShares; power++) 
                value = value.add(this.random().multiply( 
                        big(x).modPow(big(power), prime))); 
            shares.put(x, value); 
        } 
  
        return shares; 
    } 
  
    /** 
     * @param shares 
     *            Key-Value pairs of data points 
     * @return the secret number 
     */
    public BigInteger join(HashMap<Integer, BigInteger> shares) { 
        BigInteger secret = big(0); 
        for (int a : shares.keySet()) { 
            BigInteger numerator = big(1); 
            BigInteger denominator = big(1); 
            for (int b : shares.keySet()) { 
                if (a == b) 
                    continue; 
                numerator = numerator.multiply(big(-b)).mod(prime); 
                denominator = denominator.multiply(big(a - b)).mod(prime); 
            } 
            secret = (prime.add(secret).add(shares.get(a).multiply(numerator) 
                    .multiply(denominator.modInverse(prime)))).mod(prime); 
        } 
        return secret; 
    } 
}