package com.alpha.orderservice.security;

import com.nimbusds.jose.jwk.RSAKey;

public interface RSAKeyProvider {
    RSAKey getRSAKey();
}
