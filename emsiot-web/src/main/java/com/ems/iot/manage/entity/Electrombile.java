package com.ems.iot.manage.entity;


public class Electrombile {
    private Integer elect_id;

    private Integer gua_card_num;

    private String plate_num;

    private String ve_id_num;

    private String elect_brand;

    private String buy_date;

    private String elect_color;

    private String motor_num;

    private String note;

    private Integer pro_id;

    private Integer city_id;

    private Integer area_id;

    private Integer elect_type;

    private Integer insur_detail;

    private String elect_pic;

    private String indentity_card_pic;

    private String record_pic;

    private String install_card_pic;

    private String owner_tele;

    private String owner_name;

    private String owner_address;

    private String owner_id;

    private Integer recorder_id;

    private String recorder_time;

    private Integer elect_state;

    private Integer black_list_recorder;

    private String black_list_time;
    
    private String insur_pic;
    
    private String tele_fee_pic;

    private Integer lock_status;
    
    private String lock_address;
    
    private String lock_station;
    
    private String lock_time;
    
    private Integer alarm_sms;
    
    ////////////////////
    
    private String owner_id_type;
    private String owner_sex;
    private String owner_nationality;
    private String owner_id_issuing_authority;
    private String owner_id_useful_life;
    private String owner_pic;
    private String owner_born;
    ////////////////////
    private String user_tele;
    
	private String provinceName;
	private String cityName;
	private String areaName;
	
    public String getOwner_id_type() {
		return owner_id_type;
	}

	public void setOwner_id_type(String owner_id_type) {
		this.owner_id_type = owner_id_type;
	}

	public String getOwner_sex() {
		return owner_sex;
	}

	public void setOwner_sex(String owner_sex) {
		this.owner_sex = owner_sex;
	}

	public String getOwner_nationality() {
		return owner_nationality;
	}

	public void setOwner_nationality(String owner_nationality) {
		this.owner_nationality = owner_nationality;
	}

	public String getOwner_id_issuing_authority() {
		return owner_id_issuing_authority;
	}

	public void setOwner_id_issuing_authority(String owner_id_issuing_authority) {
		this.owner_id_issuing_authority = owner_id_issuing_authority;
	}

	public String getOwner_id_useful_life() {
		return owner_id_useful_life;
	}

	public void setOwner_id_useful_life(String owner_id_useful_life) {
		this.owner_id_useful_life = owner_id_useful_life;
	}

	public String getOwner_pic() {
		return owner_pic;
	}

	public void setOwner_pic(String owner_pic) {
		this.owner_pic = owner_pic;
	}

	public Integer getElect_id() {
        return elect_id;
    }

    public void setElect_id(Integer elect_id) {
        this.elect_id = elect_id;
    }

    public Integer getGua_card_num() {
        return gua_card_num;
    }

    public void setGua_card_num(Integer gua_card_num) {
        this.gua_card_num = gua_card_num;
    }

    public String getPlate_num() {
        return plate_num;
    }

    public void setPlate_num(String plate_num) {
        this.plate_num = plate_num == null ? null : plate_num.trim();
    }

    public String getVe_id_num() {
        return ve_id_num;
    }

    public void setVe_id_num(String ve_id_num) {
        this.ve_id_num = ve_id_num == null ? null : ve_id_num.trim();
    }

    public String getElect_brand() {
        return elect_brand;
    }

    public void setElect_brand(String elect_brand) {
        this.elect_brand = elect_brand == null ? null : elect_brand.trim();
    }

    public String getBuy_date() {
        return buy_date;
    }

    public void setBuy_date(String buy_date) {
        this.buy_date = buy_date;
    }

    public String getElect_color() {
        return elect_color;
    }

    public void setElect_color(String elect_color) {
        this.elect_color = elect_color == null ? null : elect_color.trim();
    }

    public String getMotor_num() {
        return motor_num;
    }

    public void setMotor_num(String motor_num) {
        this.motor_num = motor_num == null ? null : motor_num.trim();
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note == null ? null : note.trim();
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

    public Integer getElect_type() {
        return elect_type;
    }

    public void setElect_type(Integer elect_type) {
        this.elect_type = elect_type;
    }

    public Integer getInsur_detail() {
        return insur_detail;
    }

    public void setInsur_detail(Integer insur_detail) {
        this.insur_detail = insur_detail;
    }

    public String getElect_pic() {
        return elect_pic;
    }

    public void setElect_pic(String elect_pic) {
        this.elect_pic = elect_pic == null ? null : elect_pic.trim();
    }

    public String getIndentity_card_pic() {
        return indentity_card_pic;
    }

    public void setIndentity_card_pic(String indentity_card_pic) {
        this.indentity_card_pic = indentity_card_pic == null ? null : indentity_card_pic.trim();
    }

    public String getRecord_pic() {
        return record_pic;
    }

    public void setRecord_pic(String record_pic) {
        this.record_pic = record_pic == null ? null : record_pic.trim();
    }

    public String getInstall_card_pic() {
        return install_card_pic;
    }

    public void setInstall_card_pic(String install_card_pic) {
        this.install_card_pic = install_card_pic == null ? null : install_card_pic.trim();
    }

    public String getOwner_tele() {
        return owner_tele;
    }

    public void setOwner_tele(String owner_tele) {
        this.owner_tele = owner_tele == null ? null : owner_tele.trim();
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name == null ? null : owner_name.trim();
    }

    public String getOwner_address() {
        return owner_address;
    }

    public void setOwner_address(String owner_address) {
        this.owner_address = owner_address == null ? null : owner_address.trim();
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id == null ? null : owner_id.trim();
    }

    public Integer getRecorder_id() {
        return recorder_id;
    }

    public void setRecorder_id(Integer recorder_id) {
        this.recorder_id = recorder_id;
    }

    public String getRecorder_time() {
        return recorder_time;
    }

    public void setRecorder_time(String recorder_time) {
        this.recorder_time = recorder_time;
    }

    public Integer getElect_state() {
        return elect_state;
    }

    public void setElect_state(Integer elect_state) {
        this.elect_state = elect_state;
    }

    public Integer getBlack_list_recorder() {
        return black_list_recorder;
    }

    public void setBlack_list_recorder(Integer black_list_recorder) {
        this.black_list_recorder = black_list_recorder;
    }

    public String getBlack_list_time() {
        return black_list_time;
    }

    public void setBlack_list_time(String black_list_time) {
        this.black_list_time = black_list_time;
    }

	public String getInsur_pic() {
		return insur_pic;
	}

	public void setInsur_pic(String insur_pic) {
		this.insur_pic = insur_pic;
	}

	public String getTele_fee_pic() {
		return tele_fee_pic;
	}

	public void setTele_fee_pic(String tele_fee_pic) {
		this.tele_fee_pic = tele_fee_pic;
	}

	public String getOwner_born() {
		return owner_born;
	}

	public void setOwner_born(String owner_born) {
		this.owner_born = owner_born;
	}

	public Integer getLock_status() {
		return lock_status;
	}

	public void setLock_status(Integer lock_status) {
		this.lock_status = lock_status;
	}

	public String getLock_address() {
		return lock_address;
	}

	public void setLock_address(String lock_address) {
		this.lock_address = lock_address;
	}

	public String getLock_station() {
		return lock_station;
	}

	public void setLock_station(String lock_station) {
		this.lock_station = lock_station;
	}

	public String getLock_time() {
		return lock_time;
	}

	public void setLock_time(String lock_time) {
		this.lock_time = lock_time;
	}

	public String getUser_tele() {
		return user_tele;
	}

	public void setUser_tele(String user_tele) {
		this.user_tele = user_tele;
	}

	public Integer getAlarm_sms() {
		return alarm_sms;
	}

	public void setAlarm_sms(Integer alarm_sms) {
		this.alarm_sms = alarm_sms;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	
    
	
}