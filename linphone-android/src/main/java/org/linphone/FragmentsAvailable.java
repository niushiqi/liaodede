package org.linphone;

/*
FragmentsAvailable.java
Copyright (C) 2017  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

public enum FragmentsAvailable {
	UNKNOW,

  /**
   * 拨号器
   */
  DIALER,

  /**
   * 空界面
   */
  EMPTY,

  /**
   * 电话记录
   */
  HISTORY_LIST,

  /**
   * 记录详情页
   */
  HISTORY_DETAIL,

  /**
   * 联系人列表
   */
  CONTACTS_LIST,

  /**
   * 联系人详情页
   */
  CONTACT_DETAIL,

  /**
   * 联系人编辑页
   */
  CONTACT_EDITOR,

  /**
   * 关于界面
   */
  ABOUT,

  /**
   * 帐号设置界面
   */
  ACCOUNT_SETTINGS,

  /**
   * app设置页
   */
  SETTINGS,

  /**
   * 聊天列表
   */
  CHAT_LIST,

  /**
   * 聊天详情页
   */
  CHAT;

	public boolean shouldAnimate() {
		return true;
	}

	public boolean isRightOf(FragmentsAvailable fragment) {
		switch (this) {
		case HISTORY_LIST:
			return fragment == UNKNOW;

		case HISTORY_DETAIL:
			return HISTORY_LIST.isRightOf(fragment) || fragment == HISTORY_LIST;

		case CONTACTS_LIST:
			return HISTORY_DETAIL.isRightOf(fragment) || fragment == HISTORY_DETAIL;

		case CONTACT_DETAIL:
			return CONTACTS_LIST.isRightOf(fragment) || fragment == CONTACTS_LIST;

		case CONTACT_EDITOR:
			return CONTACT_DETAIL.isRightOf(fragment) || fragment == CONTACT_DETAIL;

		case DIALER:
			return CONTACT_EDITOR.isRightOf(fragment) || fragment == CONTACT_EDITOR;

		case CHAT_LIST:
			return DIALER.isRightOf(fragment) || fragment == DIALER;

		case SETTINGS:
			return CHAT_LIST.isRightOf(fragment) || fragment == CHAT_LIST;

		case ABOUT:
		case ACCOUNT_SETTINGS:
			return SETTINGS.isRightOf(fragment) || fragment == SETTINGS;

		case CHAT:
			return CHAT_LIST.isRightOf(fragment) || fragment == CHAT_LIST;

		default:
			return false;
		}
	}

	public boolean shouldAddItselfToTheRightOf(FragmentsAvailable fragment) {
		switch (this) {
		case HISTORY_DETAIL:
			return fragment == HISTORY_LIST || fragment == HISTORY_DETAIL;

		case CONTACT_DETAIL:
			return fragment == CONTACTS_LIST || fragment == CONTACT_EDITOR|| fragment == CONTACT_DETAIL;

		case CONTACT_EDITOR:
			return fragment == CONTACTS_LIST || fragment == CONTACT_DETAIL || fragment == CONTACT_EDITOR;

		case CHAT:
			return fragment == CHAT_LIST || fragment == CHAT;

		default:
			return false;
		}
	}
}
