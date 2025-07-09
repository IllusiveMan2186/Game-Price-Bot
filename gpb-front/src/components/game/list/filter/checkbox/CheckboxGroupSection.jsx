import './CheckboxGroupSection.css';

export default function CheckboxGroupSection({
  title,
  options,
  fieldName,
  fieldValue,
  isNotExcludedFieldType,
  onChange,
  isChecked
}) {
  return (
    <div className="App-game-filter-section">
      <div className="App-game-filter-title">{title}</div>
      <div className="App-game-filter-genre">
        {options.map((option) => (
          <label key={option.value} className="App-game-filter-genre-button">
            <input
              type="checkbox"
              className="App-game-filter-genre-button-checkbox"
              name={fieldName}
              value={option.value}
              onChange={(e) => onChange(e, isNotExcludedFieldType)}
              defaultChecked={isChecked(option.value, fieldValue, isNotExcludedFieldType)}
            />
            <span className="App-game-filter-genre-button-text">{option.label}</span>
          </label>
        ))}
      </div>
    </div>
  );
}
