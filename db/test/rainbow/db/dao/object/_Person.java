package rainbow.db.dao.object;

import java.time.LocalDate;

import rainbow.core.model.object.IdNameObject;

public class _Person extends IdNameObject<Integer> {

	private LocalDate birthday;
	
	private _Gender gender;
	
	private Integer[] score = new Integer[3];

	public LocalDate getBirthday() {
		return birthday;
	}

	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}

	public _Gender getGender() {
		return gender;
	}

	public void setGender(_Gender gender) {
		this.gender = gender;
	}

	public Integer[] getScore() {
		return score;
	}
	
}
