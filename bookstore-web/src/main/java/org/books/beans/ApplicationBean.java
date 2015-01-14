package org.books.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.books.persistence.CreditCard;
import org.books.persistence.CreditCard.Type;

/**
 *
 * @author AWy
 */
@Named("applicationBean")
@ApplicationScoped
public class ApplicationBean implements Serializable {

    private final List<Country> countriesDe = new ArrayList<>();
    private final List<Country> countriesEn = new ArrayList<>();
    private final Locale localeGerman = new Locale("de");
    private final Locale localeEnglish = new Locale("en");

    public ApplicationBean() {
	for (String countryCode : Locale.getISOCountries()) {
	    Locale locale = new Locale("", countryCode);
	    countriesDe.add(new Country(countryCode, locale.getDisplayCountry(localeGerman)));
	    countriesEn.add(new Country(countryCode, locale.getDisplayCountry(localeEnglish)));
	}
	CountryNameComparator comp = new CountryNameComparator();
	Collections.sort(countriesDe, comp);
	Collections.sort(countriesEn, comp);
	countriesDe.add(0, new Country("", ""));
	countriesEn.add(0, new Country("", ""));
    }

    public List<Country> getCountries() {
	Locale currentLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	if (localeGerman.getLanguage().equals(currentLocale.getLanguage())) {
	    return countriesDe;
	}
	return countriesEn;
    }
    
    public List<Type> getCreditCardTypes() {
	return Arrays.asList(CreditCard.Type.values());
    }

    public class Country {
	private final String code;
	private final String name;

	public Country(String code, String name) {
	    this.code = code;
	    this.name = name;
	}

	public String getCode() {
	    return code;
	}

	public String getName() {
	    return name;
	}
    }

    private class CountryNameComparator implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {
	    if (!(o1 instanceof Country) && !(o2 instanceof Country)) {
		return 0;
	    }
	    if (!(o1 instanceof Country)) {
		return 1;
	    }
	    if (!(o2 instanceof Country)) {
		return -1;
	    }

	    Country country1 = (Country) o1;
	    Country country2 = (Country) o2;

	    if (country1.getName() == null) {
		return 1;
	    }

	    return country1.getName().compareTo(country2.getName());
	}
    }
}
