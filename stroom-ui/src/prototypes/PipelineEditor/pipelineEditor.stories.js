/*
 * Copyright 2018 Crown Copyright
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
 */
import React, { Component } from 'react';
import PropTypes from 'prop-types';

import { storiesOf, addDecorator } from '@storybook/react';
import { action } from '@storybook/addon-actions';
import { withNotes } from '@storybook/addon-notes';

import { ReduxDecoratorWithInitialisation } from 'lib/storybook/ReduxDecorator';
import { DragDropDecorator } from 'lib/storybook/DragDropDecorator';

import { PipelineEditor } from './index';

import PipelineElement from './PipelineElement';
import { ElementPalette } from './ElementPalette';
import { AddElementWizard } from './AddElementToPipeline';

import { actionCreators } from './redux';

import 'styles/main.css';

import { testPipeline, testPipelineElements } from './test/pipeline.testData';
import { testElementTypes, testElementProperties } from './test/elements.testData';

const { pipelineReceived, elementsReceived, elementPropertiesReceived } = actionCreators;

storiesOf('Pipeline Editor', module)
  .addDecorator(ReduxDecoratorWithInitialisation((store) => {
    store.dispatch(pipelineReceived('testPipeline', testPipeline));
    store.dispatch(elementsReceived(testElementTypes));
    store.dispatch(elementPropertiesReceived(testElementProperties));
  })) // must be recorder after/outside of the test initialisation decorators
  .addDecorator(DragDropDecorator)
  .add('Pipeline Editor', () => <PipelineEditor pipelineId="testPipeline" />);

storiesOf('Element Palette', module)
  .addDecorator(ReduxDecoratorWithInitialisation((store) => {
    store.dispatch(elementsReceived(testElementTypes));
    store.dispatch(elementPropertiesReceived(testElementProperties));
  })) // must be recorder after/outside of the test initialisation decorators
  .addDecorator(DragDropDecorator)
  .add('Element Palette', () => <ElementPalette />);
