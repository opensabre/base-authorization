-- Keep the local-development gateway registration aligned with the
-- GATEWAY_LOCAL_OAUTH_CLIENT_SECRET default used by base-gateway.
UPDATE oauth2_registered_client
SET client_secret = '$2y$10$zjDbXviQ/DtuZ1d25DDDVux00UrjLmCGRNKL3kuEFmVxixfEpn7a.',
    updated_time = NOW(3),
    updated_by = 'system'
WHERE client_id = 'base-gateway-local'
  AND client_secret <> '$2y$10$zjDbXviQ/DtuZ1d25DDDVux00UrjLmCGRNKL3kuEFmVxixfEpn7a.';
