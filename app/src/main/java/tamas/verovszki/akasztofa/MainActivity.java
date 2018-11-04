package tamas.verovszki.akasztofa;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Button Button_Up, Button_Down, Button_Guess;
    TextView Text_View_Letter, Text_View_Word;
    ImageView Image_View;
    final String letters = "AÁBCDEÉFGHIÍJKLMNOÓÖŐPQRSTUÚÜŰVWXYZ"; // ez 35 karakterből áll, ezért 35 elemű a következő sorban a tömb
    int[] letters_color = new int[35]; // lásd feljebb
    final char underscore = '_'; // aláhúzás a ki nem talált betűk helyére
    Random random_number = new Random();
    String[] words_for_guess = new String[20]; // ennyi kitalálandó szó van "beégetve" a programba
    boolean play_again = true; // akar a játékos még egy játékot?
    String word_to_guess = ""; // kitalálandó szó
    String blind_word = ""; // kitalálandó szó, amiben a még nem kitalált betűk helyett aláhúzások vannak
    int current_char_no = 0; // az összes létező betűből álló string hanyadik poziciójánál vagyunk?  = kiválasztott betű
    int lives; // életek száma. Később adom majd meg az értékét, a játék "inicializálása" során
    boolean there_is_a_hit = false; // ha a tippelt betű megtalálható a kitalálandó szóban
    int savedTextViewColor = 0; // int típusú, értéke a fehér színtől való eltérés értéke

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT); //  elforgatott nézet tiltása
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(); // View-ek inicializálása (button-ok, textview-ek, stb.)
        fillStrings(); // tömbben tárolom a kitalálandó szavakat. Ez a metódus tölti fel a tömb elemeit.
        prepareGame(); // játék és új játék előkészítése (változók alaphelyzetbe állítása, színek beállítása, textview-ek alapállapotba hozása

        /*while (play_again){ // először ezt a ciklust akartam használni, hogy mindaddig fusson a program, amíg a játékos újra akar játszani, de
                              // nem űködött. végtelen ciklus és fehér üres képernyő fogadott. A ciklustörzsben lettek volna a gomb-események és a preparegame metódus is.
        }*/

        Button_Up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // jobb oldali, "felfele" gomb -> gombnyomásra a következő betű az ABC-ben
                current_char_no++;
                if (current_char_no == letters.length()){ // Z betű után az A jöjjön
                    current_char_no = 0;
                }
                Text_View_Letter.setTextColor(letters_color[current_char_no]); // aktuálisan kiválasztott betű szín-beállítása (narancssárga vagy fekete)
                Text_View_Letter.setText(letters.substring(current_char_no, current_char_no+1)); // aktuálisan kiválasztott betű megjelenítése
            }
        });

        Button_Down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // bal oldali, "lefele" gomb -> gombnyomásra az előző betű az ABC-ben
                current_char_no--;
                if (current_char_no == -1){ // A betű után a Z jöjjön
                    current_char_no = letters.length()-1; // itt a -1-et nem teljesen látom át hogy miért kell, de így működik jól
                }
                Text_View_Letter.setTextColor(letters_color[current_char_no]); // aktuálisan kiválasztott betű szín-beállítása (narancssárga vagy fekete)
                Text_View_Letter.setText(letters.substring(current_char_no, current_char_no+1)); // aktuálisan kiválasztott betű megjelenítése
            }
        });

        Button_Guess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // tippelés a kiválasztott betűre
                letters_color[current_char_no] = getResources().getColor(R.color.black); // ez most már már egy tippelt betű, ezért egyből feketére állítom a színt
                Text_View_Letter.setTextColor(letters_color[current_char_no]); // ld: előző sor
                evaluateChosenLetter(Text_View_Letter.getText().toString()); /* a toString kell ide, mert enélkül nem működik. De miért nem??? Úgy gondoltam, hogy a
                 Textview-ek szövege csak String lehet (mint C#-ban is). Akkor meg minek a ToString? */
            }
        });
    }

    public void init(){ // View-ek inicializálása (button-ok, textview-ek, stb.)
        Button_Down = (Button) findViewById(R.id.Button_Down);
        Button_Up = (Button) findViewById(R.id.Button_Up);
        Button_Guess = (Button) findViewById(R.id.Button_Guess);
        Text_View_Letter = (TextView) findViewById(R.id.Text_View_Letter);
        Text_View_Word = (TextView) findViewById(R.id.Text_View_Word);
        Image_View = (ImageView) findViewById(R.id.Image_View);
    }

    public void fillStrings(){ // tömbben tárolom a kitalálandó szavakat. Ez a metódus tölti fel a tömb elemeit.
        words_for_guess[0] = "LEHETETLEN";
        words_for_guess[1] = "MÉLABÚ";
        words_for_guess[2] = "BOLDOGSÁG";
        words_for_guess[3] = "FATÁNYÉR";
        words_for_guess[4] = "JAGUÁR";
        words_for_guess[5] = "VETÉLKEDŐ";
        words_for_guess[6] = "FUTÓVERSENY";
        words_for_guess[7] = "KERÉKPÁROZIK";
        words_for_guess[8] = "SZORGALMAS";
        words_for_guess[9] = "TELEVÍZIÓ";
        words_for_guess[10] = "BUDAPEST";
        words_for_guess[11] = "EJTŐERNYŐ";
        words_for_guess[12] = "SZÁRÍTÓGÉP";
        words_for_guess[13] = "BANKKÁRTYA";
        words_for_guess[14] = "MEGHÁLÁL";
        words_for_guess[15] = "BIZTONSÁGOS";
        words_for_guess[16] = "ANANÁSZ";
        words_for_guess[17] = "KÍSÉRTET";
        words_for_guess[18] = "KIEGÉSZÍT";
        words_for_guess[19] = "TIRAMISU";
    }

    public void fillLettersColors(){ // betű-színek narancssárgára állítása
        for (int i = 0; i < letters_color.length; i++){
            letters_color[i] = getResources().getColor(R.color.orange); // ide kell a getResources!!! Nehezen jöttem rá. Nem látom át, hogy miért kell, de enélkül nem íródnak át a színek.
        }
    }

    public void evaluateChosenLetter(String findThisLetter){
        StringBuilder temp_string = new StringBuilder(blind_word);
        for (int i = 0; i < word_to_guess.length(); i++) {
            /*if (word_to_guess.substring(i, i+1) == findThisLetter){ // Az == nem működik, csak az equals. Miért nem ? - Utána olvastam: a String a Java-ban objektum-típus, és mivel
            így már nem teljes a hasonlóság (2 különböző objektumról van szó), ezért az == nem ad teljes egyezést. C# annyiban más, hogy ott van string is és String is.
            Ott a string-nél működik az ==, a String-nél nem, mert a String (nagy kezdőbetűvel) ott is objektum, míg a string (kis betűvel) egy változó-típus.*/
            if (word_to_guess.substring(i, i + 1).equals(findThisLetter)) { // ha eltalálta a játékos a betűt
                there_is_a_hit = true;
                temp_string.setCharAt(i*2, findThisLetter.charAt(0)); // át kellett alakítanom char-rá. Eleve jobb lett volna char-ral dolgozni, de így alakult :( A *2 az aláhúzásjelek közötti space-k miatt kell
                Text_View_Word.setText(temp_string); // kitalált betű megjelenítése
                blind_word = temp_string.toString();
                if (blind_word.replace(" ", "").equals(word_to_guess)){ // Ha kitalálta a teljes szót. A space-ket ki kell szedni a vizsgálathoz!
                    wannaPlayAnother(getString(R.string.playerWon));
                }
            }
        }
        if (there_is_a_hit == false){ // ha nem találta el a betűt
            lives--; // életek száma csökken
            switch (lives) { // a következő akasztófa-kép megjelenítése
                case 12:
                    Image_View.setBackgroundResource(R.drawable.akasztofa01);
                    break;
                case 11:
                    Image_View.setBackgroundResource(R.drawable.akasztofa02);
                    break;
                case 10:
                    Image_View.setBackgroundResource(R.drawable.akasztofa03);
                    break;
                case 9:
                    Image_View.setBackgroundResource(R.drawable.akasztofa04);
                    break;
                case 8:
                    Image_View.setBackgroundResource(R.drawable.akasztofa05);
                    break;
                case 7:
                    Image_View.setBackgroundResource(R.drawable.akasztofa06);
                    break;
                case 6:
                    Image_View.setBackgroundResource(R.drawable.akasztofa07);
                    break;
                case 5:
                    Image_View.setBackgroundResource(R.drawable.akasztofa08);
                    break;
                case 4:
                    Image_View.setBackgroundResource(R.drawable.akasztofa09);
                    break;
                case 3:
                    Image_View.setBackgroundResource(R.drawable.akasztofa10);
                    break;
                case 2:
                    Image_View.setBackgroundResource(R.drawable.akasztofa11);
                    break;
                case 1:
                    Image_View.setBackgroundResource(R.drawable.akasztofa12);
                    break;
                case 0:
                    Image_View.setBackgroundResource(R.drawable.akasztofa13);
                    break;
                default:
                    break;
            }
        }
        there_is_a_hit = false;
        if (lives == 0){ // ha vesztett a játékos
            showWordToGuess(); // a ki nem talált szót írja ki más színnel
            wannaPlayAnother(getString(R.string.playerLost)); // Akar még egyet játszani?
        }
    }

    public void wannaPlayAnother(String question){ // Felugró ablak - akar még egyet játszani?
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(question); // Ha nyert, gratulálunk, ha nem, akkor kiírjuk hogy vesztett.
        builder1.setCancelable(false); // ne lehessen megkerülni a felugró ablakot

        builder1.setPositiveButton( // "igen" válasz esetén
                R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        play_again = true;
                        dialog.cancel();
                        prepareGame(); // játék és új játék előkészítése (változók alaphelyzetbe állítása, színek beállítása, textview-ek alapállapotba hozása
                    }
                });

        builder1.setNegativeButton( // "nem" válasz esetén
                R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        play_again = false;
                        dialog.cancel();
                        finish(); //kilépés a programból
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void prepareGame(){ // játék és új játék előkészítése (változók alaphelyzetbe állítása, színek beállítása, textview-ek alapállapotba hozása
        lives = 13; // életek visszaállítása
        current_char_no = 0; // visszaállunk az A-betűre
        Text_View_Letter.setText(letters.substring(current_char_no, current_char_no+1)); // visszaállunk az A-betűre
        fillLettersColors(); // betű-színek narancssárgára állítása
        Text_View_Letter.setTextColor(letters_color[current_char_no]); // aktuális betű szín narancssárgára állítása
        Image_View.setBackgroundResource(R.drawable.akasztofa00); // akasztófa-kép kezdőállapot
        blind_word=""; // legutóbbi kitalálandó szó törlése
        word_to_guess = words_for_guess[random_number.nextInt(20)]; // új kitalálandó szó "sorsolása"
        for (int i = 0; i < word_to_guess.length(); i++){ // a kitalálandó szó hosszának megfelelő, aláhúzásokból álló karakterlánc létrehozása.
            blind_word+= underscore;
            if (i != word_to_guess.length()) // a legutolsó aláhúzás után már nem kell üres karakter
                blind_word+= " "; // vizuálisan szebb, ha az aláhúzások között üres karakterek vannak
        }
        Text_View_Word.setText(blind_word); // kezdetben egyik betű sincs még kitalálva
    }
    public void showWordToGuess(){ // a ki nem talált szó megmutatása
        savedTextViewColor = Text_View_Word.getCurrentTextColor(); // előző szín elmentése
        Text_View_Word.setTextColor(getResources().getColor(R.color.red)); // x másodpercig piros lesz a szó
        Text_View_Word.setText(word_to_guess);
        waitForXSeconds(2000);
    }

    public void waitForXSeconds(int milliSeconds){ // ez a metódus várakozik a megadott időig
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Ide kerül az a kód, ami végrehajtódik a megadott idő letelte után
                Text_View_Word.setTextColor(savedTextViewColor); // a ki nem talált szó visszakapja eredeti színét
            }
        }, milliSeconds);
    }
}
