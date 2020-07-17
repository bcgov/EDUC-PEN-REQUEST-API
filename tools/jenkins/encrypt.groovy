@GrabResolver(name='bintray', root='https://dl.bintray.com/terl/lazysodium-maven')
@Grab('com.goterl.lazycode:lazysodium-java:4.3.0')
@Grab('info.picocli:picocli:2.0.3')
@picocli.groovy.PicocliScript

import com.goterl.lazycode.lazysodium.LazySodiumJava;
import com.goterl.lazycode.lazysodium.SodiumJava;
import com.goterl.lazycode.lazysodium.exceptions.SodiumException;
import com.goterl.lazycode.lazysodium.interfaces.SecretBox;
import com.goterl.lazycode.lazysodium.utils.Key;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;

import groovy.transform.Field
import static picocli.CommandLine.*

def encrypt(String textToEncrypt, String key) {
    def lazySodium = new LazySodiumJava(new SodiumJava(), StandardCharsets.UTF_8);
    def secretBoxLazy = (SecretBox.Lazy) lazySodium;

    byte[] nonceBytes = lazySodium.nonce(SecretBox.NONCEBYTES);
    String nonceHexString = DatatypeConverter.printHexBinary(nonceBytes);
    
    def encryptedMessage = secretBoxLazy.cryptoSecretBoxEasy(textToEncrypt, nonceBytes, Key.fromBase64String(key));
    return DatatypeConverter.parseHexBinary(encryptedMessage).encodeBase64().toString();
}

return this;
