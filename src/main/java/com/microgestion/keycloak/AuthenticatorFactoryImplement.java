package com.microgestion.keycloak;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

public class AuthenticatorFactoryImplement implements AuthenticatorFactory {

    public static final String PROVIDER_ID = "custom-authenticator";

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return new CustomAuthenticator();
    }

    @Override
    public String getReferenceCategory() {
        // Para un autenticador genérico, devolver null.
        // Si manejara contraseñas, devolverías CredentialModel.PASSWORD
        return null;
    }

    private static final Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.ALTERNATIVE,
            AuthenticationExecutionModel.Requirement.DISABLED
    };

    @Override
    public Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return null;
    }

    @Override
    public String getDisplayType() {
        return "Mi Autenticador Personalizado ( nombre que aparece en el listado ) ";
    }

    @Override
    public String getHelpText() {
        return "Este es un autenticador de ejemplo que puedes extender. ( esta es la descripcion )";
    }

    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {
    }
}