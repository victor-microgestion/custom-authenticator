package com.microgestion.keycloak;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public class CustomAuthenticator implements Authenticator {

    private static final Logger logger = Logger.getLogger(CustomAuthenticator.class);

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        logger.info(">>> Mi Autenticador Personalizado: authenticate()");
        // Aquí va tu lógica principal.
        // Por ejemplo, podrías comprobar algo y luego decidir si el flujo continúa.
        // Si todo está bien, llamas a success().
        context.success();

        // Si necesitas que el usuario ingrese datos, crearías un formulario:
        // Response challenge = context.form().createForm("mi-formulario.ftl");
        // context.challenge(challenge);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        logger.info(">>> Mi Autenticador Personalizado: action()");
        // Esta parte se ejecuta cuando un usuario envía un formulario que tú presentaste.
        // Aquí procesarías los datos del formulario.
    }

    @Override
    public boolean requiresUser() {
        // Devuelve 'true' si este paso necesita que el usuario ya esté identificado
        // (por ejemplo, después de que haya introducido su nombre de usuario).
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        // Devuelve 'true' si este autenticador debe ejecutarse para este usuario/realm.
        // Puedes usarlo para activar/desactivar el autenticador condicionalmente.
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // Aquí puedes añadir acciones requeridas para el usuario, como "UPDATE_PASSWORD".
    }

    @Override
    public void close() {
        // Este método se llama para limpiar recursos, si los hubiera.
    }
}