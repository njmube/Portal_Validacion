package com.iqtb.validacion.mail;

import com.iqtb.validacion.util.Bitacoras;
import com.sun.mail.imap.IMAPSSLStore;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.pop3.POP3SSLStore;
import com.sun.mail.pop3.POP3Store;
import com.sun.net.ssl.internal.ssl.Provider;
import java.security.Security;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.log4j.Logger;

/**
 *
 * @author danielromero
 */
public class Email {

    private static Logger logger = Logger.getLogger(Email.class);

    public static boolean sendEmailAuth(String remitente, String contrasenia, String destinatario, String asunto, String contenido, String host, String ssl, String puerto) {
        boolean respuesta = false;
        try {
            // se obtiene el objeto Session. La configuración es para una cuenta de gmail.
            logger.info("sendEmailAuth - Obteniendo la sesión HOST: " + host + " TLS: " + ssl + " PORT: " + puerto + " USER: " + remitente);
            Properties props = new Properties();
            props.put("mail.smtp.host", host); //"smtp.gmail.com"
            props.setProperty("mail.smtp.starttls.enable", ssl);
//            props.setProperty("mail.smtp.ssl.enable", ssl);
            props.setProperty("mail.smtp.port", puerto);
            props.setProperty("mail.smtp.user", remitente);
            props.setProperty("mail.smtp.auth", "true");

            Session session = Session.getDefaultInstance(props, null);
//            session.setDebug(true);

            // Se compone la parte del texto
            BodyPart texto = new MimeBodyPart();
            texto.setText("Esté correo electrónico es enviado desde la pagína Validación de CFDIs.\n" + contenido);

            // Se compone el adjunto con la imagen
//            BodyPart adjunto = new MimeBodyPart();
//            adjunto.setDataHandler(
//                new DataHandler(new FileDataSource("/Users/danielromero/AAA010101AAA.xml")));
//            adjunto.setFileName("xml.xml");
            // Una MultiParte para agrupar texto e imagen.
            MimeMultipart multiParte = new MimeMultipart();
            multiParte.addBodyPart(texto);
//            multiParte.addBodyPart(adjunto);

            // Se compone el correo, dando to, from, subject y el
            // contenido.
            logger.info("sendEmailAuth -  Se compone el correo electrónico TO: " + destinatario + " FROM: " + remitente);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente)); //remitente
            message.addRecipient(
                    Message.RecipientType.TO,
                    new InternetAddress(destinatario)); //destinatario
            message.setSubject(asunto); //asunto
            message.setContent(multiParte);

            // Se envia el correo.
            logger.info("sendEmailAuth - Enviando...");
            Transport t = session.getTransport("smtp");
            t.connect(remitente, contrasenia);
            t.sendMessage(message, message.getAllRecipients());
            t.close();
            respuesta = true;
            logger.info("sendEmailAuth - Enviando");
        } catch (MessagingException ex) {
            String desc = "sendEmailAuth - MessagingException ERROR " + ex.getMessage();
            Bitacoras.registrarBitacora(null, null, null, desc, "ERROR");
            logger.error("sendEmailAuth - MessagingException ERROR " + ex);
        }
        return respuesta;
    }

    public static boolean conPOP3(String servidor, String puerto, String usuario, String pass, String tls, String ssl) {
        boolean con = false;
        try {
            logger.info("conectPOP3 - Obteniendo la sesión HOST: " + servidor + " PORT: " + puerto + " SSL: " + ssl + "TLS: " + tls);
            Properties prop = System.getProperties();
            prop.setProperty("mail.pop3.ssl.enable", ssl);
            prop.setProperty("mail.pop3.starttls.enable", tls);
            prop.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            prop.setProperty("mail.pop3.socketFactory.fallback", "false");
            prop.setProperty("mail.pop3.ssl.trust", servidor);
            prop.setProperty("mail.pop3.port", puerto);
            prop.setProperty("mail.pop3.socketFactory.port", puerto);
            Session sesion = Session.getInstance(prop);
//            sesion.setDebug(true);
            logger.info("conectPOP3 - Conectando...");
            Store store = sesion.getStore("pop3");
            store.connect(servidor, usuario, pass);

            con = store.isConnected();

        } catch (MessagingException e) {
            String desc = "conectPOP3 - MessagingException ERROR " + e;
            Bitacoras.registrarBitacora(null, null, null, desc, "ERROR");
            logger.error("conectPOP3 - ERROR " + e);
        }
        return con;
    }

    public static boolean conIMAP(String servidor, String puerto, String usuario, String pass, String tls, String ssl) {
        boolean con = false;
        try {
            logger.info("conectIMAP - Obteniendo la sesión HOST: " + servidor + " PORT: " + puerto + " SSL: " + ssl + " TLS: " + tls);
            Properties prop = System.getProperties();
            prop.setProperty("mail.imap.ssl.enable", ssl);
            prop.setProperty("mail.imap.starttls.enable", tls);
            prop.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            prop.setProperty("mail.imap.ssl.trust", servidor);
            prop.setProperty("mail.imap.port", puerto);
            prop.setProperty("mail.imap.socketFactory.port", puerto);
            Session sesion = Session.getInstance(prop);

            logger.info("conectIMAP - Conectando...");
            Store store = sesion.getStore("imaps");
            store.connect(servidor, usuario, pass);

            con = store.isConnected();
        } catch (MessagingException e) {
            String desc = "conectIMAP - MessagingException ERROR " + e.getMessage();
            Bitacoras.registrarBitacora(null, null, null, desc, "ERROR");
            logger.error("conectIMAP ERROR " + e);
        }
        return con;
    }

    public static boolean connect(String server, String username, String password, String port, boolean sslConnection, String tipoServidor)
            throws Exception {
        logger.info("Creando conexion");
        int puerto = Integer.parseInt(port);
        Properties pop3Props = new Properties();

        if (sslConnection) {
            pop3Props.setProperty("mail." + tipoServidor + ".socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            pop3Props.setProperty("mail." + tipoServidor + ".socketFactory.fallback", "false");
            pop3Props.setProperty("mail." + tipoServidor + ".socketFactory.port", port);
        }
        pop3Props.setProperty("mail." + tipoServidor + ".port", String.valueOf(port));
        pop3Props.setProperty("mail." + tipoServidor + ".connectiontimeout", String.valueOf(30000));
        pop3Props.setProperty("mail." + tipoServidor + ".timeout", String.valueOf(30000));
        pop3Props.setProperty("mail." + tipoServidor + ".ssl.protocols", "TLSv1"); //SSLv3 TLSv1

        if (sslConnection) {
            Security.addProvider(new Provider());
            pop3Props.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
        }

        URLName url = new URLName(tipoServidor, server, puerto, "", username, password);

        Session session = Session.getInstance(pop3Props, null);
        Store store;
        if (sslConnection) {
            if (tipoServidor.equals("pop3")) {
                store = new POP3SSLStore(session, url);
            } else {
                store = new IMAPSSLStore(session, url);
            }
        } else {
            if (tipoServidor.equals("pop3")) {
                store = new POP3Store(session, url);
            } else {
                store = new IMAPStore(session, url);
            }
        }
        session.setDebug(true);
        logger.info(new StringBuilder().append("Conectando con ").append(server).append(":").append(port).append(" user: ").append(username).toString());
        store.connect();
        return store.isConnected();
    }

}
