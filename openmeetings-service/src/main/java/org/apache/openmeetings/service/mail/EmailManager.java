/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.service.mail;

import static org.apache.openmeetings.db.util.ApplicationHelper.ensureApplication;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_EMAIL_AT_REGISTER;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getDefaultLang;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWicketApplicationName;

import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.core.mail.MailHandler;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.service.mail.template.RegisterUserTemplate;
import org.apache.wicket.Application;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author swagner
 *
 */
@Component
public class EmailManager {
	private static final Logger log = Red5LoggerFactory.getLogger(EmailManager.class, getWebAppRootKey());

	@Autowired
	private ConfigurationDao cfgDao;
	@Autowired
	private MailHandler mailHandler;

	private static IApplication getApp() {
		return (IApplication)Application.get(getWicketApplicationName());
	}

	public static String getString(String key) {
		return getApp().getOmString(key);
	}

	/**
	 * sends a mail address to the user with his account data
	 *
	 * @param username - login of the registered user
	 * @param email - email of the registered user
	 * @param hash - activation hash
	 * @param sendEmailWithVerficationCode - if email with verification code should be sent
	 * @param langId - language Id
	 */
	public void sendMail(String username, String email, String hash, Boolean sendEmailWithVerficationCode, Long langId) {
		log.debug("sendMail:: username = {}, email = {}", username, email);
		boolean sendEmailAtRegister = cfgDao.getBool(CONFIG_EMAIL_AT_REGISTER, false);

		ensureApplication(langId != null ? langId : getDefaultLang());
		String link = getApp().urlForActivatePage(new PageParameters().add("u",  hash));

		if (sendEmailAtRegister) {
			mailHandler.send(email, getString("512")
				, RegisterUserTemplate.getEmail(username, email, sendEmailWithVerficationCode ? link : null));
		}
	}
}
