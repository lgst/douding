package com.ddgj.dd.bean;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;

/**
 * 个人用户信息实体类
 * @author Administrator
 *
 */
public class PersonalUser extends User implements Serializable {

	/**
	 * password : 123456
	 * user_head :
	 * user_name :
	 * user_id : cb5e22ea-1528-46bb-9cf2-f527ea9b3d25
	 * user_qq :
	 * user_address :
	 * user_integral :
	 * account : lgst
	 * account_id : dd12dd17-fa9d-4547-b3a5-7c8eae5382fa
	 * account_type : 0
	 * add_time :
	 * modify_time :
	 * nickname :
	 * user_sex :
	 * user_age :
	 * phone_number :
	 * user_email :
	 * id_number :
	 * id_number_picture :
	 * ip :
	 * user_landing_time :
	 * user_bank_account :
	 * role_id :
	 * user_permissions :
	 */

	private String password;
	/**用头像*/
	private String user_head;
	/**姓名*/
	private String user_name;
	/**用户id*/
	private String user_id;
	private String user_qq;
	private String user_address;
	private String user_integral;
	private String add_time;
	private String modify_time;
	/***/
	private String nickname;
	private String user_sex;
	private String user_age;
	private String phone_number;
	private String user_email;
	private String id_number;
	private String id_number_picture;
	private String ip;
	private String user_landing_time;
	private String user_bank_account;
	private String role_id;
	private String user_permissions;

	public void saveToSharedPreferences(Context context)
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences("user",Context.MODE_PRIVATE);
		sharedPreferences.edit()
				.putString("password",password)
				.putString("user_head",user_head)
				.putString("user_name",user_name)
				.putString("user_id",user_id)
				.putString("user_qq",user_qq)
				.putString("user_address",user_address)
				.putString("user_integral",user_integral)
				.putString("account",account)
				.putString("account_id",account_id)
				.putString("account_type",account_type)
				.putString("add_time",add_time)
				.putString("modify_time",modify_time)
				.putString("nickname",nickname)
				.putString("user_sex",user_sex)
				.putString("user_age",user_age)
				.putString("phone_number",phone_number)
				.putString("user_email",user_email)
				.putString("id_number",id_number)
				.putString("id_number_picture",id_number_picture)
				.putString("ip",ip)
				.putString("user_landing_time",user_landing_time)
				.putString("user_bank_account",user_bank_account)
				.putString("role_id",role_id)
				.putString("user_permissions",user_permissions).commit();
	}
	/**从SharedPreferences获取个人用户信息*/
	public void initFromSharedPreferences(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
		password = sharedPreferences.getString("password", "");
		user_head = sharedPreferences.getString("user_head", "");
		user_name = sharedPreferences.getString("user_name", "");
		user_id = sharedPreferences.getString("user_id", "");
		user_qq = sharedPreferences.getString("user_qq", "");
		user_address = sharedPreferences.getString("user_address", "");
		user_integral = sharedPreferences.getString("user_integral", "");
		account = sharedPreferences.getString("account", "");
		account_id = sharedPreferences.getString("account_id", "");
		account_type = sharedPreferences.getString("account_type", "");
		add_time = sharedPreferences.getString("add_time", "");
		modify_time = sharedPreferences.getString("modify_time", "");
		nickname = sharedPreferences.getString("nickname", "");
		user_sex = sharedPreferences.getString("user_sex", "");
		user_age = sharedPreferences.getString("user_age", "");
		phone_number = sharedPreferences.getString("phone_number", "");
		user_email = sharedPreferences.getString("user_email", "");
		id_number = sharedPreferences.getString("id_number", "");
		id_number_picture = sharedPreferences.getString("id_number_picture", "");
		ip = sharedPreferences.getString("ip", "");
		user_landing_time = sharedPreferences.getString("user_landing_time", "");
		user_bank_account = sharedPreferences.getString("user_bank_account", "");
		role_id = sharedPreferences.getString("role_id", "");
		user_permissions = sharedPreferences.getString("user_permissions", "");
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUser_head() {
		return user_head;
	}

	public void setUser_head(String user_head) {
		this.user_head = user_head;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_qq() {
		return user_qq;
	}

	public void setUser_qq(String user_qq) {
		this.user_qq = user_qq;
	}

	public String getUser_address() {
		return user_address;
	}

	public void setUser_address(String user_address) {
		this.user_address = user_address;
	}

	public String getUser_integral() {
		return user_integral;
	}

	public void setUser_integral(String user_integral) {
		this.user_integral = user_integral;
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

	public String getModify_time() {
		return modify_time;
	}

	public void setModify_time(String modify_time) {
		this.modify_time = modify_time;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getUser_sex() {
		return user_sex;
	}

	public void setUser_sex(String user_sex) {
		this.user_sex = user_sex;
	}

	public String getUser_age() {
		return user_age;
	}

	public void setUser_age(String user_age) {
		this.user_age = user_age;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}

	public String getUser_email() {
		return user_email;
	}

	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}

	public String getId_number() {
		return id_number;
	}

	public void setId_number(String id_number) {
		this.id_number = id_number;
	}

	public String getId_number_picture() {
		return id_number_picture;
	}

	public void setId_number_picture(String id_number_picture) {
		this.id_number_picture = id_number_picture;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUser_landing_time() {
		return user_landing_time;
	}

	public void setUser_landing_time(String user_landing_time) {
		this.user_landing_time = user_landing_time;
	}

	public String getUser_bank_account() {
		return user_bank_account;
	}

	public void setUser_bank_account(String user_bank_account) {
		this.user_bank_account = user_bank_account;
	}

	public String getRole_id() {
		return role_id;
	}

	public void setRole_id(String role_id) {
		this.role_id = role_id;
	}

	public String getUser_permissions() {
		return user_permissions;
	}

	public void setUser_permissions(String user_permissions) {
		this.user_permissions = user_permissions;
	}
}
