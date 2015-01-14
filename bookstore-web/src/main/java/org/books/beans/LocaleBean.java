package org.books.beans;

import java.io.Serializable;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author AWy
 */
@Named("localeBean")
@SessionScoped
public class LocaleBean implements Serializable {

    private Locale locale;

    @PostConstruct
    public void init() {
	FacesContext context = FacesContext.getCurrentInstance();
	locale = context.getApplication().getViewHandler().calculateLocale(context);
    }

    public Locale getLocale() {
	return locale;
    }

    public void setGerman() {
	changeLocale(Locale.GERMAN);
    }

    public void setEnglish() {
	changeLocale(Locale.ENGLISH);
    }

    public boolean isShowGerman() {
	return !Locale.GERMAN.equals(getLocale());
    }

    public boolean isShowEnglish() {
	return !Locale.ENGLISH.equals(getLocale());
    }

    private void changeLocale(Locale locale) {
	this.locale = locale;
    }
}
