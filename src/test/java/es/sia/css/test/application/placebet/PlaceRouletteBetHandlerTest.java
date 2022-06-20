package es.sia.css.test.application.placebet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import es.sia.css.test.domain.roulette.NumberRandomizer;
import es.sia.css.test.domain.roulette.Roulette;
import es.sia.css.test.domain.roulette.RouletteColor;
import es.sia.css.test.domain.user.User;
import es.sia.css.test.domain.user.User.NotEnoughCashException;
import es.sia.css.test.domain.valueobject.Cash;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PlaceRouletteBetHandlerTest {
    NumberRandomizer numberRandomizerMock = mock(NumberRandomizer.class);

    @Test
    void winningColorBet() {
        User user = User.of(Cash.of(100L));

        PlaceRouletteBetHandler subject = new PlaceRouletteBetHandler(
            user,
            Roulette.of(numberRandomizerMock)
        );

        when(numberRandomizerMock.getNumber(37)).thenReturn(6);

        PlaceColorRouletteBetCommand command = new PlaceColorRouletteBetCommand(1L, RouletteColor.BLACK);
        subject.handle(command);

        Assertions.assertEquals(101L, user.getCash().value());
    }

    @Test
    void notEnoughCashExceptionWhenLosingColorBet() {
        User user = User.of(Cash.of(100L));

        PlaceRouletteBetHandler subject = new PlaceRouletteBetHandler(
            user,
            Roulette.of(numberRandomizerMock)
        );

        when(numberRandomizerMock.getNumber(37)).thenReturn(7);

        // Para que se lance la excepción, el amount de la apuesta tiene que ser mayor al cash
        PlaceColorRouletteBetCommand command = new PlaceColorRouletteBetCommand(110L, RouletteColor.RED);

        Assertions.assertThrows(NotEnoughCashException.class, () -> subject.handle(command));
    }

    @Test
    void losingColorBet() {
        User user = User.of(Cash.of(100L));

        PlaceRouletteBetHandler subject = new PlaceRouletteBetHandler(
            user,
            Roulette.of(numberRandomizerMock)
        );

        when(numberRandomizerMock.getNumber(37)).thenReturn(7);

        PlaceColorRouletteBetCommand command = new PlaceColorRouletteBetCommand(50L, RouletteColor.BLACK);
        subject.handle(command);

        Assertions.assertEquals(50L, user.getCash().value());
    }

    @Test
    void winningNumericBet() {
        long userCash = 100;
        long betAmount = 20;
        long cashAfterBet = userCash - betAmount + (35 * betAmount);
        User user = User.of(Cash.of(userCash));

        PlaceRouletteBetHandler subject = new PlaceRouletteBetHandler(
                user,
                Roulette.of(numberRandomizerMock)
        );

        when(numberRandomizerMock.getNumber(37)).thenReturn(6);

        PlaceSingleNumberRouletteBetCommand command = new PlaceSingleNumberRouletteBetCommand(betAmount, 6);
        subject.handle(command);

        Assertions.assertEquals(cashAfterBet, user.getCash().value());
    }

    @Test
    void losingNumericBet() {
        long userCash = 100;
        long betAmount = 20;
        long cashAfterBet = userCash - betAmount;
        User user = User.of(Cash.of(userCash));

        PlaceRouletteBetHandler subject = new PlaceRouletteBetHandler(
                user,
                Roulette.of(numberRandomizerMock)
        );

        when(numberRandomizerMock.getNumber(37)).thenReturn(6);

        PlaceSingleNumberRouletteBetCommand command = new PlaceSingleNumberRouletteBetCommand(betAmount, 7);
        subject.handle(command);

        Assertions.assertEquals(cashAfterBet, user.getCash().value());
    }

}