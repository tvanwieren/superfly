package com.payneteasy.superfly.model.ui.subsystem;

import com.payneteasy.superfly.model.ui.smtp_server.UISmtpServerForFilter;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

public class UISubsystem implements Serializable {
	private Long id;
	private String name;
    private String title;
	private String callbackUrl;
	private boolean allowListUsers;
    private UISmtpServerForFilter smtpServer;
    private String subsystemUrl;
    private String landingUrl;

	@Column(name = "ssys_id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "subsystem_name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    @Column(name = "subsystem_title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "callback_information")
	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	@Column(name = "allow_list_users")
	public boolean isAllowListUsers() {
		return allowListUsers;
	}

	public void setAllowListUsers(boolean allowListUsers) {
		this.allowListUsers = allowListUsers;
	}

    @ManyToOne
    @JoinColumn(table = "smtp_server", name = "ssrv_id")
    public UISmtpServerForFilter getSmtpServer() {
        return smtpServer;
    }

    public void setSmtpServer(UISmtpServerForFilter smtpServer) {
        this.smtpServer = smtpServer;
    }

    @Column(name = "subsystem_url")
    public String getSubsystemUrl() {
        return subsystemUrl;
    }

    public void setSubsystemUrl(String subsystemUrl) {
        this.subsystemUrl = subsystemUrl;
    }

    @Column(name = "landing_url")
    public String getLandingUrl() {
        return landingUrl;
    }

    public void setLandingUrl(String landingUrl) {
        this.landingUrl = landingUrl;
    }
}
