package my.app.views.masterdetailperson;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import my.app.data.entity.SamplePerson;
import my.app.data.service.SamplePersonService;
import my.app.views.MainLayout;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;

@PageTitle("Master Detail Person")
@Route(value = "master-detail-person", layout = MainLayout.class)
@AnonymousAllowed
@Uses(Icon.class)
public class MasterDetailView extends SplitLayout implements HasUrlParameter<Long> {


    private final Grid<SamplePerson> grid = new Grid<>(SamplePerson.class, false);

    private TextField firstName = new TextField("First Name");
    private TextField lastName = new TextField("Last Name");
    private TextField email = new TextField("Email");
    private TextField phone = new TextField("Phone");
    private DatePicker dateOfBirth = new DatePicker("Date Of Birth");
    private TextField occupation = new TextField("Occupation");
    private TextField role = new TextField("Role");
    private Checkbox important = new Checkbox("Important");;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<SamplePerson> binder;

    private final SamplePersonService service;

    public MasterDetailView(SamplePersonService samplePersonService) {
        this.service = samplePersonService;
        buildView();

        // Connect Grid to the backend
        listPersonsInGrid();

        // Configure form binding
        binder = new BeanValidationBinder<>(SamplePerson.class);
        binder.bindInstanceFields(this);

        addListeners();

    }

    private void addListeners() {
        grid.asSingleSelect().addValueChangeListener(event -> {
            var selectedPerson = event.getValue();
            if(selectedPerson == null) {
                prepareFormForNewPerson();
            } else {
                editPerson(selectedPerson);
            }
        });

        cancel.addClickListener(e -> {
            prepareFormForNewPerson();
            listPersonsInGrid();
        });

        save.addClickListener(e -> {
            try {
                service.update(binder.getBean());
                prepareFormForNewPerson();
                listPersonsInGrid();
                Notification.show("Data updated");
            } catch (ObjectOptimisticLockingFailureException exception) {
                showErrorMessage("Error updating the data. Somebody else has updated the record while you were making changes.");
            }
        });
        save.addClickShortcut(Key.ENTER);
    }

    private void showErrorMessage(String errorMessage) {
        Notification n = Notification.show(errorMessage);
        n.setPosition(Notification.Position.MIDDLE);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private void buildView() {
        setOrientation(Orientation.HORIZONTAL);
        setSizeFull();

        var formLayout = new FormLayout(firstName, lastName, email, phone, dateOfBirth, occupation, role, important);
        formLayout.setClassName("editor");

        var buttonLayout = new HorizontalLayout(save, cancel);
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var editorLayout = new VerticalLayout();
        editorLayout.setWidth("400px");
        editorLayout.setPadding(false);
        editorLayout.setSpacing(false);
        editorLayout.addAndExpand(formLayout);
        editorLayout.add(buttonLayout);

        addToSecondary(editorLayout);
        addToPrimary(grid);

        // Configure Grid
        grid.setColumns("firstName","lastName" ,"email");
        grid.addComponentColumn(p ->
                        p.isImportant() ? checkedIcon() : uncheckedIcon())
                .setHeader("Important")
                .setKey("important") // sorting falls back to "important" field in DTO
                .setSortable(true);
        grid.getColumns().forEach(c -> {
            c.setAutoWidth(true);
        });
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setSizeFull();

    }

    private void editPerson(SamplePerson person) {
        binder.setBean(person);
        updateRouteParameters();
    }

    private void listPersonsInGrid() {
        grid.setItems(query -> service.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
    }

    private void prepareFormForNewPerson() {
        editPerson(new SamplePerson());
    }

    /**
     * Updates deep linkin parameters.
     */
    private void updateRouteParameters() {
        if(isAttached()) {
            String deepLinkingUrl = RouteConfiguration.forSessionScope().getUrl(getClass(), binder.getBean().getId());
            getUI().get().getPage().getHistory()
                    .replaceState(null, deepLinkingUrl);
        }
    }

    /**
     * Called by the framework when entering the page.
     * Decodes possible deep linking parameters from the URL.
     *
     * @param event
     *            the navigation event that caused the call to this method
     * @param samplePersonId
     *            the optional person id coming in as URL parameter
     */
    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long samplePersonId) {
        /*
         * When entering the view, check if there is an
         * if an existing person should be selected for
         * editing based on the current URL
         */
        if (samplePersonId != null) {
            Optional<SamplePerson> samplePersonFromBackend = service.get(samplePersonId);
            if (samplePersonFromBackend.isPresent()) {
                editPerson(samplePersonFromBackend.get());
            } else {
                showErrorMessage("The requested samplePerson was not found, ID = %s");
                prepareFormForNewPerson();
            }
        } else {
            prepareFormForNewPerson();
        }
    }

    private static Component uncheckedIcon() {
        Icon icon = VaadinIcon.MINUS.create();
        icon.addClassNames(LumoUtility.TextColor.DISABLED, LumoUtility.IconSize.SMALL);
        return icon;
    }

    private static Component checkedIcon() {
        Icon icon = VaadinIcon.CHECK.create();
        icon.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.IconSize.SMALL);
        return icon;
    }

}
