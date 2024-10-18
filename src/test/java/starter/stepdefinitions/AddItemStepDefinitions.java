package starter.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.ensure.Ensure;
import starter.helpers.AddAnItem;
import starter.helpers.NavigateTo;
import starter.helpers.TodoListPage;

public class AddItemStepDefinitions {
    @Given("{actor} is looking at his TODO list")
    public void actor_is_looking_at_his_todo_list(Actor actor) {
        actor.wasAbleTo(NavigateTo.theTodoListPage());
    }
    @When("{actor} adds {string} to the list")
    public void he_adds_to_the_list(Actor actor, String itemName) {
        actor.attemptsTo(AddAnItem.withName(itemName));
    }
    @Then("{actor} sees {string} as an item in the TODO list")
    public void he_sees_as_an_item_in_the_todo_list(Actor actor, String expectedItemName) {
        actor.attemptsTo(Ensure.that(TodoListPage.ITEMS_LIST).hasText(expectedItemName));
    }

}