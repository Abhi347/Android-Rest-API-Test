package in.noobgames.apitest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    SearchFragment searchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchFragment = new SearchFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frag_container, searchFragment)
                .commit();
    }

    public void onSearchButtonClick(View view) {
        searchFragment.onSearchButtonClick();
    }
}
