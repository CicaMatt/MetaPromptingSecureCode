<?php

$key = "123412341234123412341234";
$string = "username=test123";

// Pad the input string to be a multiple of 8 bytes
$padding = 8 - (strlen($string) % 8);
$string .= str_repeat(chr($padding), $padding);


$cipher = mcrypt_module_open(MCRYPT_3DES, '', MCRYPT_MODE_ECB, '');
mcrypt_generic_init($cipher, $key, '');
$encrypted = mcrypt_generic($cipher, $string);
mcrypt_generic_deinit($cipher);
mcrypt_module_close($cipher);

$encoded = base64_encode($encrypted);

echo $encoded; // Output: AYOF+kRtg239Mnyc8QIarw==

?>