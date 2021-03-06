/*
 * Copyright 2017 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package stroom.pipeline.client.presenter;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import stroom.docref.DocRef;
import stroom.editor.client.presenter.EditorPresenter;
import stroom.entity.client.presenter.ContentCallback;
import stroom.entity.client.presenter.DocumentEditTabPresenter;
import stroom.entity.client.presenter.LinkTabPanelView;
import stroom.pipeline.shared.TextConverterDoc;
import stroom.security.client.api.ClientSecurityContext;
import stroom.widget.tab.client.presenter.TabData;
import stroom.widget.tab.client.presenter.TabDataImpl;

import javax.inject.Provider;

public class TextConverterPresenter extends DocumentEditTabPresenter<LinkTabPanelView, TextConverterDoc> {
    private static final TabData SETTINGS = new TabDataImpl("Settings");
    private static final TabData CONVERSION = new TabDataImpl("Conversion");

    private final TextConverterSettingsPresenter settingsPresenter;
    private final Provider<EditorPresenter> editorPresenterProvider;

    private EditorPresenter codePresenter;
    private boolean readOnly = true;

    @Inject
    public TextConverterPresenter(final EventBus eventBus,
                                  final LinkTabPanelView view,
                                  final TextConverterSettingsPresenter settingsPresenter,
                                  final Provider<EditorPresenter> editorPresenterProvider,
                                  final ClientSecurityContext securityContext) {
        super(eventBus, view, securityContext);
        this.settingsPresenter = settingsPresenter;
        this.editorPresenterProvider = editorPresenterProvider;

        settingsPresenter.addDirtyHandler(event -> {
            if (event.isDirty()) {
                setDirty(true);
            }
        });

        addTab(CONVERSION);
        addTab(SETTINGS);
        selectTab(CONVERSION);
    }

    @Override
    protected void getContent(final TabData tab, final ContentCallback callback) {
        if (SETTINGS.equals(tab)) {
            callback.onReady(settingsPresenter);
        } else if (CONVERSION.equals(tab)) {
            callback.onReady(getOrCreateCodePresenter());
//        } else if (REFERENCES_TAB.equals(tab)) {
//            entityReferenceListPresenter.read(getEntity());
//            callback.onReady(entityReferenceListPresenter);
        } else {
            callback.onReady(null);
        }
    }

    @Override
    public void onRead(final DocRef docRef, final TextConverterDoc textConverter) {
        super.onRead(docRef, textConverter);
        settingsPresenter.read(docRef, textConverter);
        if (codePresenter != null) {
            codePresenter.setText(textConverter.getData());
        }
    }

    @Override
    protected void onWrite(final TextConverterDoc textConverter) {
        settingsPresenter.write(textConverter);
        if (codePresenter != null) {
            textConverter.setData(codePresenter.getText());
        }
    }

    @Override
    public void onReadOnly(final boolean readOnly) {
        super.onReadOnly(readOnly);
        this.readOnly = readOnly;
        settingsPresenter.onReadOnly(readOnly);
        if (codePresenter != null) {
            codePresenter.setReadOnly(readOnly);
        }
    }

    @Override
    public String getType() {
        return TextConverterDoc.DOCUMENT_TYPE;
    }

    private EditorPresenter getOrCreateCodePresenter() {
        if (codePresenter == null) {
            codePresenter = editorPresenterProvider.get();
            registerHandler(codePresenter.addValueChangeHandler(event -> setDirty(true)));
            registerHandler(codePresenter.addFormatHandler(event -> setDirty(true)));
            codePresenter.setReadOnly(readOnly);
            if (getEntity() != null) {
                codePresenter.setText(getEntity().getData());
            }
        }
        return codePresenter;
    }
}
