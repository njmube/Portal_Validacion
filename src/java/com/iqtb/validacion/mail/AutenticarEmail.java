/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.mail;

/**
 *
 * @author Valentin
 */
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public final class AutenticarEmail extends Authenticator
{
  private PasswordAuthentication autentic;

  public AutenticarEmail(String username, String passwd)
  {
    autentic = new PasswordAuthentication(username, passwd);
  }

  public PasswordAuthentication getPasswordAuthentication()
  {
    return autentic;
  }
}