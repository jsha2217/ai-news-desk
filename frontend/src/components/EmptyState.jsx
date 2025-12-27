const EmptyState = ({ Icon, title, description }) => {
  return (
    <div className="text-center py-16">
      {Icon && <Icon className="w-16 h-16 text-gray-600 mx-auto mb-4" />}
      <p className="text-xl text-gray-400">{title}</p>
      {description && <p className="text-sm text-gray-500 mt-2">{description}</p>}
    </div>
  )
}

export default EmptyState
