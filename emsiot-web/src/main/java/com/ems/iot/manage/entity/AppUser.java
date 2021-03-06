package com.ems.iot.manage.entity;

import java.util.Date;

public class AppUser {
    private Long user_id;

    private String user_name;

    private String password;

    private String user_tele;

    private Date create_time;

    private Date login_time;

    private String nickname;
    
    private Integer electCount;
    
    private Integer alarnCount;
    
    private Integer pro_id;

    private Integer city_id;

    private Integer area_id;
    
    private Integer source;
    
    private String area_name;
	
    private String pro_name;
	
	private String city_name;
    
    private boolean haveAddress = false;

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name == null ? null : user_name.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getUser_tele() {
        return user_tele;
    }

    public void setUser_tele(String user_tele) {
        this.user_tele = user_tele == null ? null : user_tele.trim();
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getLogin_time() {
        return login_time;
    }

    public void setLogin_time(Date login_time) {
        this.login_time = login_time;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }

	public Integer getElectCount() {
		return electCount;
	}

	public void setElectCount(Integer electCount) {
		this.electCount = electCount;
	}

	public Integer getAlarnCount() {
		return alarnCount;
	}

	public void setAlarnCount(Integer alarnCount) {
		this.alarnCount = alarnCount;
	}

	public Integer getPro_id() {
		return pro_id;
	}

	public void setPro_id(Integer pro_id) {
		this.pro_id = pro_id;
	}

	public Integer getCity_id() {
		return city_id;
	}

	public void setCity_id(Integer city_id) {
		this.city_id = city_id;
	}

	public Integer getArea_id() {
		return area_id;
	}

	public void setArea_id(Integer area_id) {
		this.area_id = area_id;
	}

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	public boolean isHaveAddress() {
		return haveAddress;
	}

	public void setHaveAddress(boolean haveAddress) {
		this.haveAddress = haveAddress;
	}

	public String getArea_name() {
		return area_name;
	}

	public void setArea_name(String area_name) {
		this.area_name = area_name;
	}

	public String getPro_name() {
		return pro_name;
	}

	public void setPro_name(String pro_name) {
		this.pro_name = pro_name;
	}

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}
	
	
}