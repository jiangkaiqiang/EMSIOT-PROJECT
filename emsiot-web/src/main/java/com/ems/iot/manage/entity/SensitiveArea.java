package com.ems.iot.manage.entity;

import java.util.Date;

public class SensitiveArea {
    private Integer sensitive_area_id;

    private String sensitive_area_name;

    private String station_ids;

    private Date add_time;

    private String list_elects;
    
    private String black_list_elects;

    private String sens_start_time;

    private String sens_end_time;

    private Integer enter_num;
    
    private Integer status;
    
    private Integer pro_id;
    
    private Integer city_id;
    
    private Integer area_id;


    public Integer getSensitive_area_id() {
        return sensitive_area_id;
    }

    public void setSensitive_area_id(Integer sensitive_area_id) {
        this.sensitive_area_id = sensitive_area_id;
    }

    public String getSensitive_area_name() {
        return sensitive_area_name;
    }

    public void setSensitive_area_name(String sensitive_area_name) {
        this.sensitive_area_name = sensitive_area_name == null ? null : sensitive_area_name.trim();
    }

    public String getStation_ids() {
        return station_ids;
    }

    public void setStation_ids(String station_ids) {
        this.station_ids = station_ids == null ? null : station_ids.trim();
    }

    public Date getAdd_time() {
        return add_time;
    }

    public void setAdd_time(Date add_time) {
        this.add_time = add_time;
    }

    public String getBlack_list_elects() {
        return black_list_elects;
    }

    public void setBlack_list_elects(String black_list_elects) {
        this.black_list_elects = black_list_elects == null ? null : black_list_elects.trim();
    }

    public String getSens_start_time() {
        return sens_start_time;
    }

    public void setSens_start_time(String sens_start_time) {
        this.sens_start_time = sens_start_time;
    }

    public String getSens_end_time() {
        return sens_end_time;
    }

    public void setSens_end_time(String sens_end_time) {
        this.sens_end_time = sens_end_time;
    }

    public Integer getEnter_num() {
        return enter_num;
    }

    public void setEnter_num(Integer enter_num) {
        this.enter_num = enter_num;
    }

	

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getList_elects() {
		return list_elects;
	}

	public void setList_elects(String list_elects) {
		this.list_elects = list_elects;
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
    
	
}