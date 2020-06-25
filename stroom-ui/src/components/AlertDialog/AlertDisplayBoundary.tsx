import * as React from "react";
import {
  createContext,
  FunctionComponent,
  useCallback,
  useContext,
  useState,
} from "react";
import AlertDialog, { Alert } from "./AlertDialog";

type AlertContextType = {
  alert: Alert;
  setAlert: (value: Alert) => void;
};

const AlertContext = createContext<AlertContextType | undefined>(undefined);

export const useAlert = () => {
  const { setAlert } = useContext(AlertContext);

  const alert = useCallback(
    (alert: Alert) => {
      setAlert(alert);
    },
    [setAlert],
  );

  return { alert };
};

const AlertOutlet: FunctionComponent = () => {
  const { alert, setAlert } = useContext(AlertContext);

  return (
    <AlertDialog
      alert={alert}
      isOpen={alert !== undefined}
      onCloseDialog={() => setAlert(undefined)}
    />
  );
};

export const AlertDisplayBoundary: FunctionComponent = ({ children }) => {
  const [alert, setAlert] = useState<Alert | undefined>(undefined);
  return (
    <AlertContext.Provider value={{ alert, setAlert }}>
      {children}
      <AlertOutlet />
    </AlertContext.Provider>
  );
};