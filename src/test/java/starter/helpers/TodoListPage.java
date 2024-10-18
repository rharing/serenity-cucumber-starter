package starter.helpers;

import net.serenitybdd.annotations.DefaultUrl;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.targets.Target;

@DefaultUrl("https://todomvc.com/examples/angular/dist/browser/#/all")
public class TodoListPage extends PageObject {
    public static Target ITEM_NAME_FIELD = Target.the("item name field").locatedBy(".new-todo");

    public static Target ITEMS_LIST = Target.the(" item list").locatedBy(".todo-list li");
}