package com.pathfinder.view.components;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.vaadin.hene.popupbutton.PopupButton;

import com.pathfinder.util.translation.Translator;
import com.pathfinder.util.translation.TranslatorSpec;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * MenuBar with buttons for language, appointment, home and wheelchair-mode
 * 
 * @author alexh
 * 
 */
public class MenuBar extends CustomComponent implements MenuBarSpec {
	private final TranslatorSpec translator = Translator.getInstance();

	private final GridLayout layout = new GridLayout(3, 1);
	private final VerticalLayout popupLayout = new VerticalLayout();
	private final PopupButton languagePopupButton = new PopupButton();
	private final Button wheelChairButton = new Button();
	private final Button homeButton = new Button();

	// Saves all flag image resources to avoid unnecessary reloading of
	// resources
	private Map<Locale, Resource> flagResources = new HashMap<Locale, Resource>();
	// Saves all flag images to allow adding of listeners in presenter class
	private Map<Locale, Image> flagImages = new HashMap<Locale, Image>();

	private final String THEME_RESOURCES_FOLDER = "icon/";
	private final String THEME_RESOURCES_SUFFIX = ".png";
	private final String STYLE_CLASS_LANGUAGE_BUTTON = "languageButton";
	private final String STYLE_CLASS_FLAG_IMAGE = "flagImage";

	public MenuBar() {
		buildLanguagePopup();
		buildWheelChairDriver();
		buildMainLayout();
		this.addStyling();
		this.setPrimaryStyleName("menubar");
		setCompositionRoot(layout);
	}

	private void buildLanguagePopup() {
		// load flag resources
		String flagFilename;
		for (Locale locale : translator.getSupportedLocales()) {
			flagFilename = THEME_RESOURCES_FOLDER + locale.getLanguage()
					+ THEME_RESOURCES_SUFFIX;
			flagResources.put(locale, new ThemeResource(flagFilename));
		}

		// build language popup
		Image flagImage;
		for (Locale locale : flagResources.keySet()) {
			flagImage = new Image();
			flagImage.setPrimaryStyleName(STYLE_CLASS_FLAG_IMAGE);
			flagImage.setSource(flagResources.get(locale));
			flagImages.put(locale, flagImage);
			popupLayout.addComponent(flagImage);
		}

		languagePopupButton.setContent(popupLayout);
		languagePopupButton.setPrimaryStyleName(STYLE_CLASS_LANGUAGE_BUTTON);
		String recentFlagFilename = THEME_RESOURCES_FOLDER
				+ UI.getCurrent().getLocale().getLanguage()
				+ THEME_RESOURCES_SUFFIX;
		languagePopupButton.setIcon(new ThemeResource(recentFlagFilename));
	}

	private void buildWheelChairDriver() {
		wheelChairButton
				.setIcon(new ThemeResource("icon/wheelChairDriver.png"));
		wheelChairButton.setPrimaryStyleName("wheelChairButtonMan");
	}

	private void addStyling() {
		this.setPrimaryStyleName("menu-bar");
		layout.setPrimaryStyleName("menulayout");
		popupLayout.setPrimaryStyleName(STYLE_CLASS_LANGUAGE_BUTTON);
	}

	private void buildMainLayout() {
		layout.addComponent(languagePopupButton, 0, 0);
		layout.addComponent(wheelChairButton, 2, 0);
		layout.setComponentAlignment(languagePopupButton, Alignment.TOP_CENTER);
		layout.setComponentAlignment(wheelChairButton, Alignment.TOP_CENTER);
		layout.setSizeFull();
	}

	@Override
	public void addClickListenerFlagPopup(Locale locale,
			com.vaadin.event.MouseEvents.ClickListener listener) {
		flagImages.get(locale).addClickListener(listener);
	}

	@Override
	public void addClickListenerHomeButton(ClickListener listener) {
		homeButton.addClickListener(listener);
	}

	@Override
	public void addClickListenerWheelChairButton(ClickListener listener) {
		wheelChairButton.addClickListener(listener);
	}

	@Override
	public void hideOpenLanguagePopup() {
		languagePopupButton.setPopupVisible(false);
	}

	@Override
	public void showWheelChairButton() {
		this.wheelChairButton.setVisible(true);
	}

	@Override
	public void hideWheelChairButton() {
		this.wheelChairButton.setVisible(false);
	}

	@Override
	public void showHomeButton() {
		this.homeButton.setVisible(true);
	}

	@Override
	public void hideHomeButton() {
		this.homeButton.setVisible(false);
	}

	@Override
	public void replaceWheelChairButtonWithHomeButton() {
		if (wheelChairButton.equals(layout.getComponent(2, 0))) {
			layout.replaceComponent(wheelChairButton, homeButton);
			layout.setComponentAlignment(homeButton, Alignment.TOP_RIGHT);
		}
		homeButton.setPrimaryStyleName("homebutton");
		ThemeResource res = new ThemeResource(THEME_RESOURCES_FOLDER + "home"
				+ THEME_RESOURCES_SUFFIX);
		homeButton.setIcon(res);
	}

	@Override
	public void replaceHomeButtonWithWheelChairButton() {
		if (homeButton.equals(layout.getComponent(2, 0))) {
			layout.replaceComponent(homeButton, wheelChairButton);
			layout.setComponentAlignment(wheelChairButton, Alignment.TOP_RIGHT);
		}
	}

	@Override
	public void updateTranslations() {
		languagePopupButton.setIcon(flagResources.get(UI.getCurrent()
				.getLocale()));
	}
}