package com.payneteasy.superfly.web.wicket.page.user;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.payneteasy.superfly.dao.DaoConstants;
import com.payneteasy.superfly.model.ui.role.UIRoleForFilter;
import com.payneteasy.superfly.model.ui.subsystem.UISubsystemForFilter;
import com.payneteasy.superfly.model.ui.user.UIUserForList;
import com.payneteasy.superfly.service.RoleService;
import com.payneteasy.superfly.service.SubsystemService;
import com.payneteasy.superfly.service.UserService;
import com.payneteasy.superfly.web.wicket.component.PagingDataView;
import com.payneteasy.superfly.web.wicket.component.RoleChoiceRenderer;
import com.payneteasy.superfly.web.wicket.component.SubsystemChoiceRenderer;
import com.payneteasy.superfly.web.wicket.component.label.DateLabels;
import com.payneteasy.superfly.web.wicket.page.BasePage;
import com.payneteasy.superfly.web.wicket.repeater.BaseDataProvider;

/**
 * Displays a list of users.
 * 
 * @author Roman Puchkovskiy
 */
public class ListUsersPage extends BasePage {
	
	@SpringBean
	private UserService userService;
	@SpringBean
	private RoleService roleService;
	@SpringBean
	private SubsystemService subsystemService;

	public ListUsersPage() {
		super();
		
		final UserFilters userFilters = new UserFilters();
		Form<UserFilters> filtersForm = new Form<UserFilters>("filters-form");
		add(filtersForm);
		filtersForm.add(new TextField<String>("username-filter",
				new PropertyModel<String>(userFilters, "usernamePrefix")));
		DropDownChoice<UIRoleForFilter> roleDropdown = new DropDownChoice<UIRoleForFilter>("role-filter",
				new PropertyModel<UIRoleForFilter>(userFilters, "role"),
				roleService.getRolesForFilter(), new RoleChoiceRenderer());
		roleDropdown.setNullValid(true);
		filtersForm.add(roleDropdown);
		DropDownChoice<UISubsystemForFilter> subsystemDropdown = new DropDownChoice<UISubsystemForFilter>("subsystem-filter",
				new PropertyModel<UISubsystemForFilter>(userFilters, "subsystem"),
				subsystemService.getSubsystemsForFilter(), new SubsystemChoiceRenderer());
		subsystemDropdown.setNullValid(true);
		filtersForm.add(subsystemDropdown);
		
		IDataProvider<UIUserForList> usersDataProvider = new BaseDataProvider<UIUserForList>() {
			public Iterator<? extends UIUserForList> iterator(int first,
					int count) {
				UIRoleForFilter role = userFilters.getRole();
				return userService.getUsers(userFilters.getUsernamePrefix(),
						role == null ? null : role.getId(), null, first, count,
						DaoConstants.DEFAULT_SORT_FIELD_NUMBER, true).iterator();
			}

			public int size() {
				UIRoleForFilter role = userFilters.getRole();
				return userService.getUsersCount(userFilters.getUsernamePrefix(),
						role == null ? null : role.getId(), null);
			}

		};
		DataView<UIUserForList> usersDataView = new PagingDataView<UIUserForList>("usersList", usersDataProvider) {
			@Override
			protected void populateItem(Item<UIUserForList> item) {
				final UIUserForList user = item.getModelObject();
				item.add(new Label("user-name", user.getUsername()));
				Link<Void> switchLockedStatusLink = new Link<Void>("switch-locked-status") {
					@Override
					public void onClick() {
						if (user.isAccountLocked()) {
							userService.unlockUser(user.getId());
							info("User unlocked: " + user.getUsername());
						} else {
							userService.lockUser(user.getId());
							info("User locked: " + user.getUsername());
						}
					}
				};
				switchLockedStatusLink.add(new Label("locked-status", user.isAccountLocked() ? "Yes" : "No"));
				item.add(switchLockedStatusLink);
				item.add(new Label("logins-failed", String.valueOf(user.getLoginsFailed())));
				item.add(DateLabels.forDateTime("last-login-date", user.getLastLoginDate()));
				
				PageParameters editParameters = new PageParameters();
				editParameters.add("userId", String.valueOf(user.getId()));
				item.add(new BookmarkablePageLink<EditUserPage>("edit-user",
						EditUserPage.class, editParameters));
				item.add(new Link<Void>("delete-user") {
					@Override
					public void onClick() {
						userService.deleteUser(user.getId());
						info("User deleted: " + user.getUsername());
					}
				});
			}
		};
		add(usersDataView);
		
		add(new PagingNavigator("paging-navigator", usersDataView));
		
		add(new BookmarkablePageLink<AddUserPage>("add-user", AddUserPage.class));
	}

	@SuppressWarnings("unused")
	private class UserFilters implements Serializable {
		private String usernamePrefix;
		private UIRoleForFilter role;
		private UISubsystemForFilter subsystem;

		public String getUsernamePrefix() {
			return usernamePrefix;
		}

		public void setUsernamePrefix(String usernamePrefix) {
			this.usernamePrefix = usernamePrefix;
		}

		public UIRoleForFilter getRole() {
			return role;
		}

		public void setRole(UIRoleForFilter role) {
			this.role = role;
		}

		public UISubsystemForFilter getSubsystem() {
			return subsystem;
		}

		public void setSubsystem(UISubsystemForFilter subsystem) {
			this.subsystem = subsystem;
		}
	}

}
