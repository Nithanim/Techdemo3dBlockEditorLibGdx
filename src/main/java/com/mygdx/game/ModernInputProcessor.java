package com.mygdx.game;

import com.badlogic.gdx.InputProcessor;

public interface ModernInputProcessor extends InputProcessor {
    default boolean keyDown (int keycode) {
        return false;
    }

    default boolean keyUp (int keycode) {
        return false;
    }

    default boolean keyTyped (char character) {
        return false;
    }

    default boolean touchDown (int screenX, int screenY, int pointer, int button) {
        return false;
    }

    default boolean touchUp (int screenX, int screenY, int pointer, int button) {
        return false;
    }

    default boolean touchDragged (int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    default boolean mouseMoved (int screenX, int screenY) {
        return false;
    }

    @Override
    default boolean scrolled (float amountX, float amountY) {
        return false;
    }
}
