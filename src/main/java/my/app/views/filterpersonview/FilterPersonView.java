package my.app.views.filterpersonview;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import my.app.data.entity.SamplePerson;
import my.app.data.service.SamplePersonService;
import my.app.views.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("Filter Person")
@Route(value = "filter-person", layout = MainLayout.class)
@AnonymousAllowed
@Uses(Icon.class)
public class FilterPersonView extends Div {

    private final Grid<SamplePerson> grid = new Grid<>(SamplePerson.class, false);

    private SamplePerson samplePerson;
    private DatePicker start;
    private DatePicker end;
    private Div filterLayout;

    private final SamplePersonService samplePersonService;

    @Autowired
    public FilterPersonView(SamplePersonService samplePersonService) {
        this.samplePersonService = samplePersonService;
        setSizeFull();
        addClassNames("filter-person-view");

        // Mobile version
        HorizontalLayout mobileFilters = new HorizontalLayout();
        mobileFilters.setWidthFull();
        mobileFilters.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.BoxSizing.BORDER, LumoUtility.AlignItems.CENTER);
        mobileFilters.addClassName("mobile-filters");

        Icon mobileIcon = new Icon("lumo", "plus");
        Span filtersHeading = new Span("Filters");
        mobileFilters.add(mobileIcon, filtersHeading);
        mobileFilters.setFlexGrow(1, filtersHeading);
        mobileFilters.addClickListener(e -> {
            if (filterLayout.getClassNames().contains("visible")) {
                filterLayout.removeClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:plus");
            } else {
                filterLayout.addClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:minus");
            }
        });

        VerticalLayout layout = new VerticalLayout(mobileFilters, createFilters(), createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);
    }

    private Component createFilters() {
        filterLayout = new Div();
        filterLayout.setWidthFull();
        filterLayout.addClassName("filter-layout");
        filterLayout.addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM, LumoUtility.BoxSizing.BORDER);

        TextField nameFilter = new TextField("Name");
        nameFilter.setPlaceholder("First or last name");

        TextField phoneFilter = new TextField("Phone");

        MultiSelectComboBox<String> occupationFilter = new MultiSelectComboBox<>("Occupation");
        occupationFilter.setItems("Item 1", "Item 2", "Item 3");

        CheckboxGroup<String> roleFilter = new CheckboxGroup<>("Role");
        roleFilter.setItems("Worker", "Supervisor", "Management", "External");
        roleFilter.addClassName("double-width");

        // Action buttons
        Button resetBtn = new Button("Reset");
        resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button searchBtn = new Button("Search");
        searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Div actions = new Div(resetBtn, searchBtn);
        actions.addClassName(LumoUtility.Gap.SMALL);
        actions.addClassName("actions");

        filterLayout.add(nameFilter, phoneFilter, createDateRangeFilter(), occupationFilter, roleFilter, actions);
        return filterLayout;
    }



    private Component createDateRangeFilter() {
        ComboBox<String> dateType = new ComboBox<>("Date");
        dateType.setItems("Date of birth", "Start date");
        dateType.setWidthFull();

        start = new DatePicker();
        start.setPlaceholder("From");
        start.setWidth("9rem");

        end = new DatePicker();
        end.setPlaceholder("To");
        end.setWidth("9rem");

        // aria-label for screen readers
        start.getElement()
                .executeJs("const start = this.inputElement;"
                        + "start.setAttribute('aria-label', 'From date');"
                        + "start.removeAttribute('aria-labelledby');");
        end.getElement()
                .executeJs("const end = this.inputElement;"
                        + "end.setAttribute('aria-label', 'To date');"
                        + "end.removeAttribute('aria-labelledby');");

        FlexLayout dateRangeComponent = new FlexLayout(dateType, start, new Text(" – "), end);
        dateRangeComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
        dateRangeComponent.addClassName(LumoUtility.Gap.XSMALL);
        dateRangeComponent.addClassName("double-width");

        return dateRangeComponent;
    }

    private Component createGrid() {
        grid.addColumn("firstName").setAutoWidth(true);
        grid.addColumn("lastName").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("phone").setAutoWidth(true);
        grid.addColumn("dateOfBirth").setAutoWidth(true);
        grid.addColumn("occupation").setAutoWidth(true);
        LitRenderer<SamplePerson> importantRenderer = LitRenderer.<SamplePerson>of(
                        "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", important -> important.isImportant() ? "check" : "minus").withProperty("color",
                        important -> important.isImportant()
                                ? "var(--lumo-primary-text-color)"
                                : "var(--lumo-disabled-text-color)");

        grid.addColumn(importantRenderer).setHeader("Important").setAutoWidth(true);

        grid.setItems(query -> samplePersonService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        return grid;
    }

}
