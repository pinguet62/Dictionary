package fr.pinguet62.dictionary.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DualListModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.pinguet62.dictionary.model.Profile;
import fr.pinguet62.dictionary.model.Right;
import fr.pinguet62.dictionary.service.ProfileService;
import fr.pinguet62.dictionary.service.RightService;

@Component
@ManagedBean
@ViewScoped
public final class ProfilesManagedBean {

    private List<Profile> profiles;

    @Autowired
    private ProfileService profileService;

    /**
     * The {@link Right} association (available/associated) of the selected
     * {@link Profile}.
     */
    private DualListModel<Right> rightsAssociation;

    @Autowired
    private RightService rightService;

    /** The {@link Profile} to display or update. */
    private Profile selectedProfile;

    /**
     * Get the list of {@link Profile}s to display.
     *
     * @return The {@link Profile}s.
     */
    public List<Profile> getList() {
        return profiles;
    }

    public DualListModel<Right> getRightsAssociation() {
        return rightsAssociation;
    }

    public Profile getSelectedProfile() {
        return selectedProfile;
    }

    /** Initialize the list of {@link Profile}s. */
    @PostConstruct
    private void init() {
        profiles = profileService.getAll();
    }

    /**
     * Call when the user click on "Submit" button into "Edit" dialog.
     * <p>
     * Save the modified {@link Profile}.
     */
    public void save() {
        profileService.update(selectedProfile);
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Profile updated",
                        selectedProfile.getTitle()));
    }

    public void setRightsAssociation(DualListModel<Right> rightsAssociation) {
        this.rightsAssociation = rightsAssociation;
    }

    /**
     * Call when the user click on "Show" button.
     * <ul>
     * <li>Set the selected {@link Profile};</li>
     * <li>Initialize the {@link Right} association.</li>
     * </ul>
     *
     * @param profile
     *            The selected {@link Profile}.
     */
    public void setSelectedProfile(Profile profile) {
        selectedProfile = profile;

        List<Right> availableRights = rightService.getAll().stream()
                .filter(right -> !selectedProfile.getRights().contains(right))
                .collect(Collectors.toList());
        List<Right> associatedRights = new ArrayList<>(
                selectedProfile.getRights());
        rightsAssociation = new DualListModel<Right>(availableRights,
                associatedRights);
    }

}
